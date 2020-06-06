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


## Example

#### Simple table

```java
TokenAlgorithm<Integer, String> algorithm = new ShuntingYard<>();                                // create Shunting Yard algorithm to build expression

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
Result(x=2020, y=null, value=2.775, name=X_AB)                                                   // result for 2020 = 2.275 
```

#### 