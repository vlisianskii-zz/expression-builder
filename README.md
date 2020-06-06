<h1 align="center">Expression builder</h1>

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Java CI with Maven](https://github.com/vlisianskii/expression-builder/workflows/Java%20CI%20with%20Maven/badge.svg)

Expression builder is a Java library that helps to compute simple and complex expressions.


## Getting Started
```xml
<dependency>
    <groupId>org.vl</groupId>
    <artifactId>expression-builder</artifactId>
    <version>1.0</version>
</dependency>
```


## Examples

More examples:
- [Simple Expression Tests](https://github.com/vlisianskii/expression-builder/blob/master/src/test/java/vl/SimpleExpressionTest.java)
- [Tricky Expression Tests](https://github.com/vlisianskii/expression-builder/blob/master/src/test/java/vl/TrickyExpressionTest.java) 

#### Basic expression

```java
// create Shunting Yard algorithm to build expression
TokenAlgorithm<Integer, String> algorithm = new ShuntingYard<>();

SimpleExpression expression = new SimpleExpression(expressionName, "1+(-2/3)*4", algorithm);
// compute basic expression
Result<Integer, String> result = expression.calculate();

// Output
Result(x=null, y=null, value=-1.6666666666666665, name=AB)                                        
```

#### Simple table

```java
// create a table
//     2020  2021
//  A  3.0   1.0
//  B  4.0   2.0                                                                 
ValueTable<Integer, String> table = new SimpleTable();
table.addValue(2021, "B", 2.0);
table.addValue(2020, "A", 3.0);
table.addValue(2020, "B", 4.0);
table.addValue(2021, "A", 1.0);

// build simple expression
SimpleExpression expression = new SimpleExpression("AB", "2 / (A + B)", algorithm);
// compute expression for 2020 
Result<Integer, String> result = expression.calculate(table, 2020);

// Output
Result(x=2020, y=null, value=0.2857142857142857, name=AB)
```

#### Traverse by X axis 

```java
// traverse column by column
table.traverse((x) -> {
    Result<Integer, String> result = expression.calculate(table, x);
});

// Output
Result(x=2020, y=null, value=0.2857142857142857, name=AB)
Result(x=2021, y=null, value=0.6666666666666666, name=AB)
```

#### Custom variables

```java
// create custom variables map
Map<String, Double> customVariable = newHashMap();
customVariable.put("X", 12.1);

SimpleExpression expression = new SimpleExpression("X_AB", "(X - A)/B * 0.4", algorithm);
// add custom variables into compute engine
Result<Integer, String> result = expression.calculate(table, 2020, customVariable);

// Output 
Result(x=2020, y=null, value=0.91, name=X_AB)
```

#### Constants

```java
SimpleExpression expression = new SimpleExpression(expressionName, "MONTHS_COUNT/PI", algorithm);
Result<Integer, String> result = expression.calculate();

// Output  
Result(x=null, y=null, value=3.819718638570153, name=expression with constants)
```

### Tricky Expression

Used to compute expressions for all values in the table. Traverses by X and Y axises

```java
Function<Integer, String>[] functions = new Function[]{new PriorFunction()};
// compute engine replaces word "self" by current cell
TrickyExpression expression = new TrickyExpression(expressionName, "prior(self)/self", algorithm, functions);
// traverse by X and Y
table.traverseEach((x, y) -> {
    Result<Integer, String> result = expression.calculate(table, x, y);
});

// Output  
esult(x=2020, y=A, value=3.0, name=complex expression)
Result(x=2020, y=B, value=4.0, name=complex expression)
Result(x=2021, y=A, value=3.0, name=complex expression)
Result(x=2021, y=B, value=1.8181818181818181, name=complex expression)
```