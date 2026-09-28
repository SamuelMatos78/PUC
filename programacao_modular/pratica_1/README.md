# Prática 1 — Oficina mecânica

Aplicação Java de console para gerenciar mecânicos, boxes, serviços e ordens de serviço.

## Executar

É necessário ter um JDK instalado. A partir desta pasta:

```bash
mkdir -p out
javac -encoding UTF-8 -d out src/br/com/pucminas/oficina/*.java
java -cp out br.com.pucminas.oficina.Main
```

O programa cria três mecânicos e três boxes ao iniciar. Use a opção 2 para associá-los.
As categorias disponíveis nos boxes iniciais são `Mecânica`, `Elétrica` e `Funilaria`.
Ao cadastrar uma ordem, informe a categoria do serviço correspondente ao box desejado.

Uma ordem começa aberta e sem box. Ao ser atribuída, passa para em execução. A opção 8
finaliza a ordem, libera a capacidade do box e preserva nela o box e o mecânico usados.
A listagem de um box mostra todas as ordens já atribuídas, inclusive as finalizadas,
e informa o total. O box mantém apenas as ordens em execução; o histórico é obtido
pelas ordens que guardam a referência ao box.

## Verificar regras de negócio

```bash
javac -encoding UTF-8 -d out src/br/com/pucminas/oficina/*.java test/br/com/pucminas/oficina/*.java
java -cp out br.com.pucminas.oficina.OficinaTest
```
