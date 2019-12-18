# Commons

Common classes, annotations and utilities for the OrbisData project are grouped in this module.

### Printer

The `printer` package contains classes used to transform Object into their string representation. 
Often used in `asType(Class)` method.

### Annotations

The annotations are used to improve the comprehension of the behaviour of classes and methods.
To configure IntelliJ IDE to recognize those annotations:
 - Go to `File` --> `Settings...`
 - Then `Build, Execution, Deployment` --> `Compiler`
 - Click on `Configure annotations...`
 - Add and select(annotation used for code generation) `org.orbisgis.commons.NotNull` in `NotNull annotations` box and 
 `org.orbisgis.commons.Nullable` in `Nullable annotations` box.