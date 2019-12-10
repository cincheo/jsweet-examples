# JSweet examples 

![](https://github.com/cincheo/jsweet-examples/workflows/Build%20examples%20with%20Gradle/badge.svg)]

A set of simple examples to show what can be done in Java with the [JSweet transpiler](https://github.com/cincheo/jsweet).

All these example are written in Java using the JSweet APIs (candies)] They are then transpiled to JavaScript by the JSweet transpiler.

- Simple canvas drawing (`canvasdrawing`): demonstrates the use of HTML5 canvas. [browse](http://examples.jsweet.org/jsweet-examples/webapp/canvasdrawing/index.html)
- Simple HTML form control (`inputcontrol`): demonstrates the use of HTML5 forms and inputs. [browse](http://examples.jsweet.org/jsweet-examples/webapp/inputcontrol/index.html)
- Simple jQuery (`jquery`): demonstrates the use of JQuery with JSweet. [browse](http://examples.jsweet.org/jsweet-examples/webapp/jquery/index.html)
- Simple Angular (`angularjs`): demonstrates the use of Angular with JSweet. [browse](http://examples.jsweet.org/jsweet-examples/webapp/angularjs/index.html)
- Simple Knockout (`knockoutjs`): demonstrates the use of Knockout with JSweet. [browse](http://examples.jsweet.org/jsweet-examples/webapp/knockoutjs/index.html)
- Ray tracer (`raytracer`): draws a 3D scene, adapted from the TypeScript example page. [browse](http://examples.jsweet.org/jsweet-examples/webapp/raytracer/index.html)
- Todos (`todomvc`): demonstrates the use of Backbone and Underscore with JSweet, adapted from the TypeScript example page. [browse](http://examples.jsweet.org/jsweet-examples/webapp/todomvc/index.html)
- Blocks game (`blocksgame`): demonstrates how to write an HTML5 mobile game with JSweet. [browse](http://examples.jsweet.org/jsweet-examples/webapp/blocksgame/index.html)
- Promises (`promises`): demonstrates the use of the latest EcmaScript6 Promise API. [browse](http://examples.jsweet.org/jsweet-examples/webapp/promises/index.html)

Visit the live JSweet's example page (http://www.jsweet.org/examples) to browse the examples, run them and debug the Java code within your favorite browser. Note that all these examples are responsive and should work as well on a Web browser and on a mobile.

Visit also https://github.com/cincheo/jsweet-examples-threejs for some examples using the Threejs framework (WebGL-powered 3D)]

## Usage

```
> git clone https://github.com/cincheo/jsweet-examples.git
> cd jsweet-examples
```

### Build with Gradle
```
./gradlew jsweetClean jsweet (--refresh-dependencies) (--info)
```
### Build with Maven
```
mvn generate-sources
```
### Build with Ant
```
ant
```

### Run in your favorite browser
```
> firefox webapp/${example-name}/index.html
```

## Prerequisites

The `node` and `npm` executables must be in the path (https://nodejs.org)]
Install Maven (https://maven.apache.org/install.html)]
