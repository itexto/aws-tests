# aws-tests
Ferramentas de testes relacionados aos serviços da AWS usadas pela itexto

# Escrevendo seus testes

O primeiro passo consiste em criar sua suite de testes, que é um arquivo no formato XML. Atualmente temos apenas quatro tipos de testes envolvendo os serviços S3 e SQS, no entanto a idéia é ir aumentando a variedade dos testes conforme nossas necessidades demandem.

Nota importante: neste primeiro release não estamos incluindo no repositório nossos testes automatizados. 

## Formato do arquivo

Todos os testes encontram-se contidos na tag <aws-tests>. Atualmente fornecemos testes apenas para os serviços S3 e SQS.

### Tags de testes

A tag que usamos para definir nossos testes é ```<test>```, que possuí os atributos a seguir:

* type - O tipo de teste a ser executado. Atualmente há apenas SQSTest e S3Test
* id - O nome do teste
* endpoint - Opcional, aponta para a URL do endpoint caso esteja usando alguma ferramenta como ElasticMQ ou S3Ninja

Para todos os testes você também deve definir suas credenciais de acesso. Para tal usamos a tag <credentials> que possuí dois atributos:

* accessKey - Seu access key da AWS
* secretKey - Seu secret key da AWS

### S3 - Simple Storage Service

Duas tags fundamentais: <bucket> e <action>. A primeira define qual o bucket que iremos testar, e a segunda o que desejamos verificar.

Tag <bucket> possuí dois atributos:

* name - O nome do bucket
* region - Em que região o bucket se encontra

Tag <test> Já contém alguns atributos a mais:

* type - Qual o tipo de teste? Valores disponíveis: get|put
* key - Qual o identificador do conteúdo no bucket?
* file - Opcional: para quando estamos submetendo um arquivo para o bucket. Diz o nome deste arquivo, que pode ser o caminho completo ou relativo ao diretório no qual o usuário se encontra no momento.

O corpo da tag coresponde ao conteúdo que iremos validar em nossos testes.

#### Exemplos:

Verificando a submissão de um arquivo para o S3

```xml
<test type="S3Test" id="Send file to S3">
        <bucket name="itexto" region="us-east-1"/>
        <credentials 
            accessKey="access key" 
            secretKey="secret key "/>
        <action type="put" key="itexto-key-test-file" file="input.txt"/>
    </test>
```

Verificando se um arquivo que submetemos para o S3 possuí o conteúdo esperado

```xml
  <test type="S3Test" id="Get file from S3">
        <bucket name="itexto" region="us-east-1"/>
        <credentials 
            accessKey="access key" 
            secretKey="secret key"/>
        <action type="get" key="itexto-key-test-file">test</action>
    </test>
```

### SQS - Simple Queue Service

Duas tags fundamentais: <queue> e <action>. Vamos primeiro à tag <queue> que possuí dois atributos:

* name - O nome da fila
* region - A região na qual a fila se encontra

Já a tag <action> define quais os testes que executaremos. Esta possuí os seguintes atributos:

* type - Qual teste a ser executado? Opções: publish|receive

O corpo da tag corresponde ao conteúdo que desejamos publicar ou receber em nosso teste.

Exemplo: testando o envio de mensagens

```xml
<test type="SQSTest" id="Publish message in SQS">
        <queue name="itexto" region="us-east-1"/>
        <credentials 
            accessKey="access key" 
            secretKey="secret key"/>
        <action type="publish">testData</action>
    </test>
```

Exemplo: checando o recebimento de mensagens

```xml
<test type="SQSTest" id="Receive message stored in SQS">
        <queue name="itexto" region="us-east-1"/>
        <credentials 
            accessKey="access key" 
            secretKey="secret key"/>
        <action type="receive">testData</action>
    </test>
```

### Exemplo de uma suite completa de testes

```xml
<?xml version="1.0" encoding="UTF-8"?>
<aws-tests>
    
    <test type="SQSTest" id="Publish message in SQS">
        <queue name="itexto" region="us-east-1" endpoint="sqs.us-east-1.amazonaws.com"/>
        <credentials 
            accessKey="access key" 
            secretKey="secret key"/>
        <action type="publish">testData</action>
    </test>
    
    <test type="SQSTest" id="Receive message from SQS">
        <queue name="itexto" region="us-east-1"/>
        <credentials 
            accessKey="access key" 
            secretKey="secret key"/>
        <action type="receive">testData</action>
    </test>
    
    <test type="S3Test" id="Send file to S3">
        <bucket name="itexto" region="us-east-1"/>
        <credentials 
            accessKey="access key" 
            secretKey="secret key"/>
        <action type="put" key="itexto-key-test-file" file="input.txt"/>
    </test>
    
    <test type="S3Test" id="Get file from S3">
        <bucket name="itexto" region="us-east-1"/>
        <credentials 
            accessKey="access key" 
            secretKey="secret key"/>
        <action type="get" key="itexto-key-test-file">test</action>
    </test>
</aws-tests>

```

## Construindo o projeto

Faça o clone deste repositório e construa o projeto usando Maven com o comando abaixo:

```
mvn package
```

Será gerado um arquivo JAR no diretório target que consiste no binário da aplicação.

## Como executar o S3-tests da itexto

Com Java instalado (1.7 ou posterior) execute o comando abaixo:

```java
  java -jar aws-tests-[versão].jar [arquivo de testes.xml]
´´´

O resultado será exposto no console.
