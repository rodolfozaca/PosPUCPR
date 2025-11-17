# TextAIApp

Este documento descreve a arquitetura e os principais componentes do aplicativo **TextAIApp**, com foco especial em autenticação (Firebase Authentication) e persistência no Firestore.

## Visão geral

TextAIApp é um aplicativo Android escrito em Kotlin usando Jetpack Compose para UI e um padrão arquitetural MVVM leve. O app combina persistência local (Room) com serviços do Firebase (Authentication + Firestore) e um cliente HTTP para comunicação com um modelo de IA (serviço Ollama rodando em rede local).

Principais responsabilidades:
- UI: Jetpack Compose (composables em `app/src/main/java/com/rodolfoz/textaiapp/ui/components/`).
- ViewModel: gerencia operações assíncronas e expõe callbacks para a UI (`PersonalDataViewModel`).
- Persistência local: Room (`AppDataBase`, `UserDataDao`, `DatabaseProvider`) para armazenar dados pessoais e credenciais locais (hash de senha).
- Autenticação: wrapper `AuthManager` que encapsula `FirebaseAuth` com funções suspend e utilitários de erro.
- Persistência remota: `FirebasePromptsRepository` para gravar/ler prompts em Firestore sob a coleção `users/{uid}/prompts`.
- Comunicação com IA: `OllamaApiClient` usando Ktor para enviar prompts e receber respostas (streaming). 


## Componentes e camadas

1. Camada de Apresentação (UI)
   - Arquivos: `LoginScreen.kt`, `PersonalDataUI.kt`, `PasswordSetupScreen.kt`, `PromptAndResponseUI.kt`, `PromptsListUI.kt`, `DrawerMenu.kt`, `UserInputField.kt`, `ChatResponseField.kt`, `SplashScreen.kt`.
   - Responsabilidade: renderizar a interface, coletar entrada do usuário, mostrar feedback (toasts, indicadores de carregamento) e navegar entre telas (NavHost em `MainActivity`).
   - Navegação: `NavHost` inicializada em `MainActivity` com destinos nomeados (e.g. `LoginUI`, `PromptAndResponseUI`). `MainActivity` decide o startDestination combinando o estado do Firebase (via `AuthManager.isSignedIn()`) e SharedPreferences.

2. Camada de Estado / Lógica (ViewModel)
   - `PersonalDataViewModel` + `PersonalDataViewModelFactory`
   - Responsabilidade: encapsular operações de persistência (inserir usuário, recuperar nome) e executar chamadas em `viewModelScope` para evitar bloquear a UI.

3. Camada de Dados
   - Persistência local (Room):
     - `AppDataBase` (entidade: `UserDataModel`) — contém campos de dados pessoais, `login`, `password` (hash) e `firebaseUid`.
     - `UserDataDao` com queries para buscar/atualizar usuário por id e login.
     - `DatabaseProvider` expõe singleton do DB e contém migrações (1→2 e 2→3) que adicionam colunas `login`, `password`, `firebaseUid`.
     - `UserRepository` encapsula o `UserDataDao` e oferece APIs de alto nível: inserir/atualizar usuário, definir credenciais, autenticar localmente (hash SHA-256), armazenar firebaseUid.
   - Persistência remota (Firestore):
     - `FirebasePromptsRepository` encapsula `FirebaseFirestore` com métodos suspend: `savePrompt(prompt: PromptResponse): Result<String>` e `getPrompts(userId, limit)`. Documentos salvos em `users/{uid}/prompts` com campos `userId`, `prompt`, `response`, `createdAt` (serverTimestamp).

4. Autenticação
   - `AuthManager`: objeto singleton envolvendo `FirebaseAuth`. Fornece:
     - `currentUid()`, `isSignedIn()` — estado síncrono sobre o usuário atual.
     - `signInWithEmailAndPassword`, `createUserWithEmailAndPassword`, `sendPasswordResetEmail` — wrappers suspend usando `kotlinx.coroutines.tasks.await()`.
     - `signOut()` e `mapAuthError()` para traduzir exceções Firebase em mensagens amigáveis.
   - Fluxo de autenticação no app:
     - Ao criar senha/credentials (em `PasswordSetupScreen`), o app tenta criar uma conta Firebase se houver `email` válido salvo no `UserDataModel`. Se criar com sucesso, salva `firebaseUid` localmente.
     - Ao logar (em `LoginScreen`), primeiro tenta autenticar via Firebase se houver `email` associado ao usuário local; em caso de sucesso, persiste `firebaseUid` no registro local e salva credenciais locais (login + hash) em SharedPreferences se checkbox "Lembrar login" estiver marcado.
     - Se autenticação Firebase falhar ou não existir email, há fallback para autenticação local usando `UserRepository.authenticate()` que compara hash SHA-256.
     - `MainActivity` usa `AuthManager.isSignedIn()` junto com SharedPreferences para decidir a tela inicial (pula login se usuário autenticado e "remember" estiver marcado).

5. Integração com IA
   - `OllamaApiClient` usa Ktor (OkHttp engine) para fazer POST em `http://192.168.15.166:11435/api/generate` (modelo `llama3:8b`).
   - Gera e consome stream de resposta via `bodyAsChannel()` e concatena partes JSON até `done`.
   - `MessageUtil.filterInvalidChars()` remove caracteres inválidos/ruído da resposta (substring after marker).
   - O fluxo: `PromptAndResponseUI` monta um prompt (inclui prompt de papel/role), chama `OllamaApiClient.generate()`, mostra resposta no `ChatResponseField` e, se usuário autenticado, persiste automaticamente no Firestore via `FirebasePromptsRepository.savePrompt()`.


## Formato de dados (Firestore)

Coleção e documentos:
- Root: `users` (coleção)
  - Documento: `{uid}` (documento por usuário autenticado)
    - Subcoleção: `prompts`
      - Documento: auto-id
        - Campos: `userId: string` (uid), `prompt: string`, `response: string`, `createdAt: Timestamp (serverTimestamp)`

Observações:
- A aplicação não armazena localmente as prompts do Firestore; apenas exibe quando requisitado (`PromptsListUI` chama `getPrompts(uid)`).
- `createdAt` usa `FieldValue.serverTimestamp()` ao salvar.


## Fluxos críticos (end-to-end)

1. Onboarding / Cadastro
   - `PersonalDataUI` coleta dados pessoais e chama `PersonalDataViewModel.saveUserData()` que persiste localmente (id fixo 1).
   - Depois de salvo, `MainActivity`/fluxos levam o usuário para `PasswordSetupUI` para definir `login` e `password`.
   - `PasswordSetupScreen` chama `UserRepository.setCredentialsForUser(1, login, rawPassword)` que faz hash SHA-256 e armazena no banco local.
   - Se `email` estiver presente e válido, tenta criar conta no Firebase via `AuthManager.createUserWithEmailAndPassword(email, password)`. Em caso de sucesso, grava `firebaseUid` local via `UserRepository.setFirebaseUidForUser`.

2. Login
   - `LoginScreen` obtém `login` e `password` do usuário.
   - Consulta `UserRepository.getUserByLogin(login)` para checar se existe `email` salvo.
   - Se `email` existir e válido, tenta `AuthManager.signInWithEmailAndPassword(email, password)` (Firebase). Em caso de sucesso, guarda `firebaseUid` localmente, armazena credenciais em SharedPreferences se solicitado e navega para `PromptAndResponseUI`.
   - Se Firebase falhar ou não houver `email`, faz fallback para `UserRepository.authenticate(login, password)` (comparo de hash). Se ok, navega para `PromptAndResponseUI`.

3. Uso do modelo IA e persistência de prompt
   - Usuário digita prompt na `PromptAndResponseUI`.
   - `OllamaApiClient.generate()` é chamado e resposta é processada por `MessageUtil` antes de exibir.
   - Se `AuthManager.currentUid()` retornar uid válido, app chama `FirebasePromptsRepository.savePrompt()` em background para gravar prompt/response no Firestore.


## Considerações de segurança

- Senha: o app armazena apenas hash SHA-256 localmente no `UserDataModel.password`. Não há salt explícito — recomenda-se adicionar salt + PBKDF2/Argon2 para maior segurança local.
- SharedPreferences: quando a checkbox "Lembrar login" está marcada, o app salva `saved_login` e `saved_password_hash` em `app_prefs` (não encripta). Sugerido: usar EncryptedSharedPreferences para proteção de credenciais locais.
- Comunicação com Ollama: usa HTTP sem TLS (endereço IP local). Para produção, garantir TLS e autenticação.
- Firestore rules: não há regras no repositório — é crítico configurar regras de segurança para que usuários só leiam/escrevam seus próprios prompts (ex.: allow read, write: if request.auth.uid == resource.data.userId).


## Mapas de arquivos (onde procurar o código)

- Autenticação / Firebase
  - `app/src/main/java/com/rodolfoz/textaiapp/data/AuthManager.kt`

- Persistência local (Room)
  - `app/src/main/java/com/rodolfoz/textaiapp/data/AppDataBase.kt`
  - `app/src/main/java/com/rodolfoz/textaiapp/data/UserDataDao.kt`
  - `app/src/main/java/com/rodolfoz/textaiapp/data/DatabaseProvider.kt`
  - `app/src/main/java/com/rodolfoz/textaiapp/data/UserRepository.kt`
  - `app/src/main/java/com/rodolfoz/textaiapp/data/model/UserDataModel.kt`

- Firestore (prompts)
  - `app/src/main/java/com/rodolfoz/textaiapp/data/FirebasePromptsRepository.kt`
  - `app/src/main/java/com/rodolfoz/textaiapp/data/model/PromptResponse.kt`

- Integração IA
  - `app/src/main/java/com/rodolfoz/textaiapp/domain/OllamaApiClient.kt`
  - `app/src/main/java/com/rodolfoz/textaiapp/domain/MessageUtil.kt`

- UI / Navegação
  - `app/src/main/java/com/rodolfoz/textaiapp/MainActivity.kt`
  - `app/src/main/java/com/rodolfoz/textaiapp/ui/components/*.kt`
  - `app/src/main/java/com/rodolfoz/textaiapp/ui/viewmodels/*.kt`


---