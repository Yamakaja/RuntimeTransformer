# RuntimeTransformer

A tool allowing for easy class modification at runtime, when using a normal javaagent at startup would be too inconvenient.
Note, this method comes with disadvantages, for example method modifiers may not be altered, new methods can not be created and neither can class inheritance be changed.

## Usage

Lets assume we want to inject an event handler into the `setHealth` method of `EntityLiving`,
therefore the method should something like this after transformation:

```java
public void setHealth(float newHealth) {
    ImaginaryEvent event = ImaginaryEventBus.callEvent(new ImaginaryEvent(this, newHealth));
    
    if (event.isCancelled())
        return;
        
    newHealth = event.getNewHealth();
    
    // Minecraft Code
}
```

To get there, we first need to define a transformer, this should optimally be in its own class and look something like this:

```java
@Transform(EntityLiving.class) // The class we want to transform
public class EntityLivingTransformer extends EntityLiving { // Extending EntityLiving in our transformer makes things easier, but isn't required (Which, for example, allows you to transform final classes)
    
    @Inject(TransformationType.INSERT) // Our goal is to insert code at the beginning of the method, and leave everything else intact
    public void setHealth(float newHealth) { // Then just "override" the method as usual, if it is final add an _INJECTED to the method name
        ImaginaryEvent event = ImaginaryEventBus.callEvent(new ImaginaryEvent(this, newHealth)); // Our event handling code from above
            
        if (event.isCancelled())
            return;
            
        newHealth = event.getNewHealth();
        
        throw null; // Pass execution on to the rest of the method. This will be removed at runtime but is required for compilation (At least when the method doesn't return void, so it's not necessary in this case)
        
    }
    
} 
```

And that's pretty much it, now we just need to create our runtime transformer:

```java
new RuntimeTransformer( EntityLivingTransformer.class );
```

And we're done.

You can find more examples in the example plugin.

## "Documentation"

There are three types of Injection:

- INSERT (Inserts your code at the beginning of the method)
- OVERWRITE (Overwrites the method with your code)
- APPEND (Adds code to the end of the method, only works on methods returning void)

## Compiling

Run this command to build the api project.
`gradlew jar`

If you want to build the example project add `-Pbuild-example`
`gradlew jar -Pbuild-example`
