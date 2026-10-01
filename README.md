# Duke project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Duke_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/MyBff.java` file, right-click it, and choose `Run MyBff.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____        _        
   |  _ \ _   _| | _____ 
   | | | | | | | |/ / _ \
   | |_| | |_| |   <  __/
   |____/ \__,_|_|\_\___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running a fat JAR

Install JDK 25 and ensure `java -version` reports version 25. If `JAVA_HOME` is set,
it must point to that JDK. No separate Gradle installation is needed: the included
wrapper downloads Gradle 9.2.1. The first build needs an internet connection to
download Gradle and the Shadow plugin.

From the project directory (the directory containing `build.gradle`), run:

```powershell
.\gradlew.bat shadowJar
```

On macOS or Linux, use `sh ./gradlew shadowJar` instead.

The generated fat JAR is **`build/libs/mybff.jar`**. Shadow bundles the compiled
application and any runtime dependencies into this single file. The application
currently has no external runtime dependencies. The `application` plugin supplies
the `mybff.MyBff` entry point so the JAR can be launched directly.

Run it from the project directory:

```powershell
java -jar build/libs/mybff.jar
```

Enter commands in the terminal and type `bye` to exit. Re-run `shadowJar` after
changing the source code to rebuild the JAR.

You can copy `mybff.jar` to another folder or computer and run
`java -jar mybff.jar` there with Java 25 installed. Java itself is not bundled.
Task data is stored in `data/mybff.txt` relative to the terminal's working
directory; copy that data separately if you want to keep your existing tasks.
