# JSweet examples

A set of simple examples to show what can be done with the JSweet transpiler (https://github.com/cincheo/jsweet).

All these example are written in Java using the JSweet APIs (candies). They are then transpiled to JavaScript by the JSweet transpiler.

- Simple canvas drawing (`example-name=canvasdrawing`): demonstrates the use of HTML5 canvas.
- Simple HTML form control (`inputcontrol`): demonstrates the use of HTML5 forms and inputs.
- Simple jQuery (`jquery`): demonstrates the use of JQuery with JSweet.
- Simple Angular (`angularjs`): demonstrates the use of Angular with JSweet.
- Ray tracer (`raytracer`): draws a 3D scene, adapted from the TypeScript example page.
- Todos (`todomvc`): demonstrates the use of Backbone and Underscore with JSweet, adapted from the TypeScript example page.
- Blocks game (`blocksgame`): demonstrates how to write an HTML5 mobile game with JSweet.
- Promises (`promises`): demonstrates the use of the latest EcmaScript6 Promise API.

Visit the live JSweet's example page (http://www.jsweet.org/examples) to browse the examples, run them and debug the Java code within your favorite browser. Note that all these examples are responsive and should work as well on a Web browser and on a mobile.

## Usage

```
> git clone https://github.com/cincheo/jsweet-examples.git
> cd jsweet-examples
> mvn generate-sources
> firefox webapp/${example-name}/index.html
```

## Prerequisites

The `node` and `npm` executables must be in the path (https://nodejs.org).
Install Maven (https://maven.apache.org/install.html).
