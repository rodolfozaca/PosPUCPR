# Exemplo de Projeto Dart: Programação Orientada a Objetos

Este projeto demonstra os conceitos fundamentais da Programação Orientada a Objetos (POO) em Dart, incluindo variáveis, listas, null safety, funções e classes. 

## Estrutura do Projeto

O projeto consiste em uma classe `Pessoa`, que ilustra os conceitos de encapsulamento, herança, polimorfismo e abstração. O código também contém exemplos práticos de variáveis e tipos de dados, listas, tratamento de null safety e funções.

### Funcionalidades

- **Variáveis e Tipos de Dados**: Demonstração de como declarar variáveis em Dart e os diferentes tipos de dados disponíveis.
- **Listas**: Exemplo de uso de listas e operações básicas.
- **Null Safety**: Implementação de variáveis que podem ou não ser nulas, e como lidar com isso.
- **Funções**: Criação de funções com parâmetros obrigatórios e opcionais.
- **Classes e Objetos**: Implementação de classes e como instanciar objetos.
- **POO**: Exemplo prático de encapsulamento, herança e polimorfismo.

## Código

O código pode ser encontrado no arquivo `main.dart`. Abaixo está um resumo do conteúdo:

```dart
// Importação da biblioteca Flutter
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

// Definição do aplicativo principal
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Exemplo de POO em Dart',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Página Inicial do Exemplo'),
    );
  }
}

// Página inicial do aplicativo
class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Text(
          'Exemplo de Programação Orientada a Objetos em Dart',
          style: Theme.of(context).textTheme.headlineMedium,
        ),
      ),
    );
  }
}

// Classe Pessoa para demonstrar POO
class Pessoa {
  String nome;
  int idade;

  // Construtor
  Pessoa(this.nome, this.idade);

  // Método
  void saudacao() {
    print("Olá, meu nome é $nome e tenho $idade anos.");
  }
}

