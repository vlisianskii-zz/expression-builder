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
TokenAlgorithm<Integer, String> algorithm = new ShuntingYard<>();                                // create Shunting Yard algorithm to build expression

SimpleExpression expression = new SimpleExpression(expressionName, "1+(-2/3)*4", algorithm);
Result<Integer, String> result = expression.calculate();                                         // compute basic expression

// Output
Result(x=null, y=null, value=0.2857142857142857, name=AB)                                        // result is -1.6666666666666665
```

#### Simple table

```java
ValueTable<Integer, String> table = new SimpleTable();                                           // create a table as following
table.addValue(2020, "A", 3.0);                                                                  //     2020  2021
table.addValue(2020, "B", 4.0);                                                                  //  A  3.0   1.0
table.addValue(2021, "A", 1.0);                                                                  //  B  4.0   2.0 
table.addValue(2021, "B", 2.0);

SimpleExpression expression = new SimpleExpression("AB", "2 / (A + B)", algorithm);              // build simple expression 
Result<Integer, String> result = expression.calculate(table, 2020);                              // compute expression for 2020

// Output
Result(x=2020, y=null, value=0.2857142857142857, name=AB)                                        // result for 2020 = 0.2857142857142857
```

#### Traverse by X axis 

```java
table.traverse((x) -> {                                                                          // traverse column by column
    Result<Integer, String> result = expression.calculate(table, x);                             // compute expression
});

// Output                                                                                        // resolved expression [2 / (3.0 + 4.0)]
Result(x=2020, y=null, value=0.2857142857142857, name=AB)                                        // result for 2020 = 0.2857142857142857
Result(x=2021, y=null, value=0.6666666666666666, name=AB)                                        // result for 2021 = 0.6666666666666666
```

#### Custom variables

```java
Map<String, Double> customVariable = newHashMap();                                               // create custom variables map
customVariable.put("X", 12.1);

SimpleExpression expression = new SimpleExpression("X_AB", "(X - A)/B * 0.4", algorithm);
Result<Integer, String> result = expression.calculate(table, 2020, customVariable);              // add custom variables to compute engine

// Output                                                                                        // resolved expression [(12.1 - 3.0) / 4.0 * 0.4] 
Result(x=2020, y=null, value=0.91, name=X_AB)                                                    // result for 2020 = 0.91
```

#### Constants

```java
SimpleExpression expression = new SimpleExpression(expressionName, "MONTHS_COUNT/PI", algorithm);
Result<Integer, String> result = expression.calculate();

// Output  
Result(x=null, y=null, value=3.819718638570153, name=expression with constants)                  // resolved expression [12 / 3.14159265]
```

### Tricky Expression

Used to compute expressions for all values in the table. Traverses by X and Y axises

```java
Function<Integer, String>[] functions = new Function[]{new PriorFunction()};
TrickyExpression expression = new TrickyExpression(expressionName, "prior(self)/self", algorithm, functions);
table.traverseEach((x, y) -> {                                                                  // traverse by X and Y
    Result<Integer, String> result = expression.calculate(table, x, y);
});

// Output  
esult(x=2020, y=A, value=3.0, name=complex expression)
Result(x=2020, y=B, value=4.0, name=complex expression)
Result(x=2021, y=A, value=3.0, name=complex expression)
Result(x=2021, y=B, value=1.8181818181818181, name=complex expression)
```