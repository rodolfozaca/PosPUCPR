import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

// Definição de uma classe Flutter para o aplicativo
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

// Classe Stateful para gerenciar o estado da interface do app
class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title; // Parâmetro obrigatório para o título da página

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  late Pessoa _pessoa; // Declaração de uma instância de `Pessoa`, usando o modificador `late`

  @override
  void initState() {
    super.initState();

    // Trabalhando com POO - Classes e Objetos
    // Criando uma instância da classe Pessoa
    _pessoa = Pessoa(nome: 'Alice', idade: 30);

    // Trabalhando com Arrays e Listas
    // Adicionando habilidades na lista `habilidades` do objeto `Pessoa`
    _pessoa.adicionarHabilidade('Programação');
    _pessoa.adicionarHabilidade('Design');

    // Trabalhando com Opcionais / Null Safety
    // Definindo um valor opcional para o endereço
    _pessoa.definirEndereco('Rua das Flores, 123');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            // Exibindo detalhes da pessoa instanciada
            Text(
              'Detalhes da Pessoa:',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            // Trabalhando com Variáveis e Tipos de Dados
            // Exibindo o nome e a idade, variáveis obrigatórias da classe
            Text('Nome: ${_pessoa.nome}'),
            Text('Idade: ${_pessoa.idade}'),
            // Trabalhando com Funções
            // Exibindo a idade em meses, calculada por uma função
            Text('Idade em meses: ${_pessoa.calcularIdadeEmMeses()}'),
            // Trabalhando com Null Safety
            // Exibindo o endereço da pessoa, usando operador null-aware para valores opcionais
            Text('Endereço: ${_pessoa.endereco ?? 'Endereço não especificado'}'),
            const SizedBox(height: 16),
            // Trabalhando com Arrays e Listas
            // Exibindo as habilidades da pessoa
            Text(
              'Habilidades:',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            // Mapeando as habilidades para widgets Text
            ..._pessoa.habilidades.map((habilidade) => Text(habilidade)),
          ],
        ),
      ),
    );
  }
}


class Pessoa {
  // Trabalhando com Variáveis e Tipos de Dados
  String nome; // Variável obrigatória do tipo String
  int idade; // Variável obrigatória do tipo inteiro
  double? altura; // Variável opcional (null safety) do tipo double

  List<String> habilidades = []; // Lista para armazenar as habilidades da pessoa

  String? endereco; // Variável opcional para armazenar o endereço

  // Construtor com parâmetros obrigatórios
  Pessoa({required this.nome, required this.idade, this.altura});

  // Função para adicionar uma habilidade na lista de habilidades
  void adicionarHabilidade(String habilidade) {
    habilidades.add(habilidade);
  }

  // Função para definir um endereço (pode ser nulo)
  void definirEndereco(String? novoEndereco) {
    endereco = novoEndereco;
  }

  // Função que retorna a idade em meses, trabalhando com Funções e Tipos de Dados
  int calcularIdadeEmMeses() {
    return idade * 12;
  }

  // Função estática para verificar se a pessoa é adulta, trabalhando com Funções e Métodos de Classe
  static bool verificarSeAdulto(int idade) {
    return idade >= 18;
  }
}
