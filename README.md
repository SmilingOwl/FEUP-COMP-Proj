# FEUP-COMP-Proj

Para compilar o programa, entre na pasta src/ASTFiles e corra o script make.bat
Para correr o programa já compilado, ainda na pasta src/ASTFiles corra: java Project < ../ex/\<filename\>

# Checkpoint 1:

Na pasta src/ex deixamos 3 exemplos:
 - O ExampleGram.txt é um exemplo que cobre a maioria das possibilidades da gramática, sem erros
 - O ExampleErrors.txt é um exemplo com erros nos "while"
 - O ExampleManyErrors.txt é um exemplo com muitos erros nos "while", que mostra que a partir de 10 erros o programa apenas mostra uma mensagem a dizer quantos não foram apresentados

# Checkpoint 2:

Na pasta src/ex deixamos um ficheiro (Example.txt), que é um exemplo com alguns erros semânticos.

Quanto à análise semântica, para além do pedido para este projeto, fazemos também a análise semântica de assigns, verificando que ambos os nós são do mesmo tipo ou null (no caso de funções externas não existentes na tabela de símbolos), e de returns, verificando se a variável de retorno da função tem o tipo correto.

Pode ocultar os prints da análise semântica pondo a variável "show_semantic_analysis" a false (linha 14 do ProjectClassVisitor); pode impedir a geração de código pondo a variável "show_code_generation" a false (linha 15 do ProjectClassVisitor); pode ocultar os prints da análise semântica pondo a variável "show_symbol_tables" a false (linha 8 do SymbolTablesBuilder).

O código gerado é enviado para um ficheiro chamado JVM.j, na pasta src/ASTFiles.
