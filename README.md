<h1 align="center">VL Expression builder</h1>

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Java CI with Maven](https://github.com/vlisianskii/expression-builder/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=vlisianskii_expression-builder&metric=alert_status)](https://sonarcloud.io/dashboard?id=vlisianskii_expression-builder)

A powerful expression builder that helps to compute simple and complex expressions. Supports constants, custom variables, functions, and allows to extend the default behavior.

## Functions
- Average
- Next
- Prior

## Constants
- Months count = 12
- Pi = 3.14159265

## Shunting-yard algorithm

The shunting yard algorithm is a simple technique for parsing infix expressions containing binary operators of varying precedence. 
In general, the algorithm assigns to each operator its correct operands, taking into account the order of precedence. [Wiki](https://en.wikipedia.org/wiki/Shunting-yard_algorithm) 

```text
2 * 3 / ( 2 – 1 ) + 5 * ( 4 – 1 )
```

![Image of Yaktocat](https://i.stack.imgur.com/TrHR0.png)

## Getting Started
```xml
<dependency>
    <groupId>io.github.vlisianskii</groupId>
    <artifactId>expression-builder</artifactId>
    <version>${latest.version></version>
</dependency>
```

## Examples

More examples:
- [Simple Expression Tests](https://github.com/vlisianskii/expression-builder/blob/master/src/test/java/vl/SimpleExpressionTest.java)
- [Tricky Expression Tests](https://github.com/vlisianskii/expression-builder/blob/master/src/test/java/vl/TrickyExpressionTest.java) 

### Basic expression

```java
// create Shunting Yard algorithm to build expression
TokenAlgorithm<Integer, String> algorithm = new ShuntingYard<>();

SimpleExpression expression = new SimpleExpression(<name>, "1+(-2/3)*4", algorithm);
// compute basic expression
Result<Integer, String> result = expression.calculate();

// Output
Result(value=-1.6666666666666665, name=<name>)                                        
```

#### Simple table

```java
// create a table
//    2020  2021
//  A  3.0   1.0
//  B  4.0   2.0
ValueTable<Integer, String> table = new SimpleTable();
table.addValue(2021, "B", 2.0);
table.addValue(2020, "A", 3.0);
table.addValue(2020, "B", 4.0);
table.addValue(2021, "A", 1.0);

// build simple expression
SimpleExpression expression = new SimpleExpression(<name>, "2 / (A + B)", algorithm);
// compute expression for 2020 
Result<Integer, String> result = expression.calculate(table, 2020);

// Output
Result(x=2020, value=0.2857142857142857, name=<name>)
```

#### Traverse by X axis 

```java
// traverse column by column
table.traverse((x) -> {
    Result<Integer, String> result = expression.calculate(table, x);
});

// Output
Result(x=2020, value=0.2857142857142857, name=<name>)
Result(x=2021, value=0.6666666666666666, name=<name>)
```

#### Functions

```java
// Function 'next' returns next value in the table based on current position
Function<Integer, String>[] functions = new Function[]{new NextFunction()};
SimpleExpression expression = new SimpleExpression(<name>, "A - next(B)", algorithm, functions);
table.traverse((x) -> {
    Result<Integer, String> result = expression.calculate(table, x);
});

// Output
Result(x=2020, value=1.0, name=<name>)
Result(x=2021, value=null, name=<name>) // there is no 'next' value for current 2021 by X axis
```

#### Custom variables

```java
// create custom variables map
Map<String, Double> customVariable = newHashMap();
customVariable.put("X", 12.1);

SimpleExpression expression = new SimpleExpression(<name>, "(X - A)/B * 0.4", algorithm);
// add custom variables into compute engine
Result<Integer, String> result = expression.calculate(table, 2020, customVariable);

// Output 
Result(x=2020, value=0.91, name=<name>)
```

#### Constants

```java
SimpleExpression expression = new SimpleExpression(<name>, "MONTHS_COUNT/Pi", algorithm);
Result<Integer, String> result = expression.calculate();

// Output  
Result(value=3.819718638570153, name=<name>)
```

### Tricky Expression

Used to compute expressions for all values in the table. Traverses by X and Y axises

```java
Function<Integer, String>[] functions = new Function[]{new PriorFunction()};
// compute engine replaces word "self" by current cell
TrickyExpression expression = new TrickyExpression(<name>, "prior(self)/self", algorithm, functions);
// traverse by X and Y
table.traverse((x, y) -> {
    Result<Integer, String> result = expression.calculate(table, x, y);
});

// Output  
Result(x=2020, y=A, value=3.0, name=<name>)
Result(x=2020, y=B, value=4.0, name=<name>)
Result(x=2021, y=A, value=3.0, name=<name>)
Result(x=2021, y=B, value=1.8181818181818181, name=<name>)
```