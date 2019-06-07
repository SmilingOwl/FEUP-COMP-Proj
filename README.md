# Compiler of the Java-- language to Java Bytecodes

**GROUP: 32** 

**NAME1:** Fernando Alves, **NR1:** up201605270, **GRADE1:** 18, **CONTRIBUTION1:** 25%
**NAME2:** Helena Montenegro, **NR2:** up201604184, **GRADE2:** 18, **CONTRIBUTION2:** 25%
**NAME3:** Juliana Marques, **NR3:** up201605568, **GRADE3:** 18, **CONTRIBUTION3:** 25%
**NAME4:** Ricardo Moura, **NR4:** up201604912, **GRADE4:** 18, **CONTRIBUTION4:** 25% 


**GLOBAL Grade of the project:** 18


---


**SUMMARY:**

O projeto desenvolvido tem como objetivo compilar um ficheiro com extensão .jmm, pertencente à linguagem Java-\-, gerando bytecodes do mesmo. A compilação passa pelas fases de análise sintática, análise semântica, representação intermédia e geração de código.

**EXECUTE:**

Para executar o programa e gerar os bytecodes, deve-se executar as seguintes intruções:
* `java -jar jmm.jar ex/\<file\>`
* `runjasmin`

Para testar os bytecodes gerados, executa-se: 
* `java \<file\>` 

**DEALING WITH SYNTACTIC ERRORS:**

Na fase de construção da linguagem, utilizamos um parser de tipo LL(1).
Verificamos a ocorrência de erros na expressões do tipo "while". Quando ocorre um erro na expressão de um ciclo "while", o programa ignora os tokens até encontrar um dos seguintes tokens:

* parêntesis fechado ")" - simboliza o fim da expressão do ciclo.
* chaveta aberta "{" - se o erro for a falta de parêntesis a fechar a expressão, o programa procura pelo início das instruções dentro do ciclo.
* chaveta fechada "}" - se o erro for a falta de parêntesis a fechar a expressão e não houver parêntesis a abrir, a verificação do erro pára quando encontra uma chaveta fechada que pode simbolizar o fim das instruções dentro do ciclo.
* ponto e vírgula ";" - se o erro for a falta de parêntesis a fechar e o ciclo possuir uma só instrução, sem chavetas, o programa pára quando encontra o ponto e vírgula.
* fim do ficheiro "EOF" - se o erro for a falta de parêntesis e não se encontrar um dos símbolos anteriores, a verficação pára quando se alcança o fim do ficheiro.

Os erros nas expressões while são acumulados, sendo que se existirem mais de 10, o programa apenas mostra os primeiros 10 erros.


**SEMANTIC ANALYSIS:** 

A análise semântica é efetuada utilizando um Visitor, que se encontra no ficheiro src/ASTFiles/SymbolTablesBuilder.java.
São verificados os seguintes aspetos:

* Tipos de variáveis envolvidas em instruções de adição, subtração, divisão e multiplicação são inteiros. 
* Os operandos existentes em operações de AND (&&), NOT(!) e comparações (<) são booleanos. 
* Nos assigns (=) os elementos do lado direito do assign resultam no mesmo tipo do lado esquerdo do mesmo.
* Variável não existe aquando a sua declaração.
* Tipos de retorno de funções correspondem aos tipos certos.
* Condições em "if" e ciclos "while" retornam valores booleanos.
* Atributo length é apenas aplicado a arrays.
* Não são chamadas funções de tipos primitivos (int, boolean e int[]).
* Índices de arrays no seu acesso são valores inteiros.

Incluimos o ficheiro ex/Example.txt com alguns destes erros semânticos.

**CODE GENERATION:**

A geração de código foi desenvolvida na classe ProjectClassVisitor, por intermédio de um Visitor. Criamos uma lista de variáveis locais (localVarsList) utilizada para verificar qual o número que corresponde à variável em instruções de iload.

Utilizamos a variável inMethod para colocar todo o código relativo às instruções dentro de uma função, sendo que este só será escrito no ficheiro JVM.j, após o visitor ter passado por todas as instruções. Deste modo, conseguimos colocar em locals e stack valores que permitem compilar os ficheiros sem qualquer problema.

Para colocar labels, nos ciclos e ifs, foi utilizada uma stack de labels, na qual se faz push da label quando se entra num ciclo ou num if e pop quando se sai. Esta permite ter ciclos dentro de ciclos, sem que as labels fiquem trocadas.

No uso de constantes, é verificado o seu valor e usada a instrução mais apropriada entre as existentes (iconst, bipush, sipush, ldc).

Na invocação de funções, é verificada se a variável a partir da qual se invoca existe. Se não existir, assume-se que esta diz respeito a uma classe externa e chama-se "invokestatic" em vez de "invokevirtual".

**OVERVIEW:**

Utilizamos a classe Visitor para a análise semântica e geração de código. É também utilizado Jasmin.

**TASK DISTRIBUTION:** 

Ao longo do desenvolvimento do projeto distribuimos equilibradamente no trabalho.

Durante a fase de análise sintática, para o primeiro checkpoint, a distribuição de trabalho foi a seguinte:
* Fernando - ClassDeclaration, VarDeclaration, MainDeclaration, MethodDeclaration, Type.
* Ricardo - Statement.
* Helena - Expression e verificação de erros no while.
* Juliana - Expression, comentários e verificação de erros no while.

Apesar desta distribuição, no que toca à correção de erros que surgiram nas várias partes, todos os membros do grupo participaram na mesma. Todos os membros se envolveram na geração da AST, de forma equilibrada.

Durante as fases seguintes, para o segundo checkpoint, decidimos trabalhar em paralelo na geração de código e na análise semântica. Deste modo, a distribuição de trabalho foi a seguinte:
* Fernando - geração de código para expressões aritméticas.
* Ricardo - geração de código para invocação de funções.
* Helena e Juliana - análise semântica e construção da tabela de símbolos.

Para o terceiro checkpoint:
A Helena tratou da geração de código para ciclos e ifs. O Ricardo envolveu-se em especial atenção na geração de arrays. Todo o grupo se envolveu na correção de erros em todos os apectos de geração de código existentes para permitir a geração correta de bytecodes.

Deste modo, o trabalho foi desenvolvido de forma equilibrada por todos os membros do grupo.

**PROS:**

A ferramenta é compatível com todos os exemplos de ficheiros jmm facultados pelos docentes na página moodle. 

**CONS:** 

A ferramenta não apresenta qualquer optimização.
