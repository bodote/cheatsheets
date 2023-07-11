# JAVA, JAVA_HOME, Homebrew and MacOS
## /usr/libexec/java_home
shows what is installed in /`Library/Java/JavaVirtualMachines` or `/Users/<name>/Library/Java/JavaVirtualMachines`

## Homebrew
to make java jdk's installed by homebrew be known by the `/usr/libexec/java_home` you need to make  symlink like this:
`sudo ln -sfn /opt/homebrew/opt/openjdk/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk.jdk`


```bash
echo -e "________________________________________________________
\033[34m###### Homebrew - JAVA_HOME how to  ########\033[0m
For the system Java wrappers to find this JDK, symlink it with
   sudo ln -sfn /opt/homebrew/opt/openjdk/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk.jdk
The command 
    /usr/libexec/java_home  -V
will only find those
    /usr/libexec/java_home  -X
will show a plist xml format for the same.
Openjdk is keg-only, which means it was not symlinked into /opt/homebrew,
because macOS provides similar software and installing this software in
parallel can cause   \033[31m**all kinds of trouble**\033[0m.
________________________________________________________
\033[34m#### How to remove a Java JDK form the machine #########\033[0m
You can remove the Java JDK package from /usr/libexec/java_home by deleting the directory that contains the JDK version you want to remove. Here’s an example:
    sudo rm -rf /Library/Java/JavaVirtualMachines/jdk-16.0.1.jdk
Copy This command removes the JDK version 16.0.1 from /Library/Java/JavaVirtualMachines/
________________________________________________________
\033[34m#### Java Versions installed and linkd to /Library/Java/JavaVirtualMachines/openjdk.jdk #########\033[0m
"

/usr/libexec/java_home  -V 

read -p "Are you sure you want to continue? [y/n] " -n 1 -r
echo  
if [[ $REPLY =~ ^[Nn]$ ]]
then
    echo "stopped"
    exit 1
fi
echo "ok, continue setting JAVA_HOME, GIT_RZ_SCRIPTS_SECRET, GIT_DEVS_LIST_SECRET ..."
exit 0

cat << EOF > ~/Library/LaunchAgents/setenv.allEnvVars.plist
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN"
    "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
  <plist version="1.0">
  <dict>
    <key>Label</key>
    <string>setenv.allEnvVars</string>
    <key>ProgramArguments</key>
    <array>
      <string>/bin/launchctl</string>
      <string>setenv</string>
      <string>JAVA_HOME</string>
      <string>$(/usr/libexec/java_home -v17)</string>

      <string>/bin/launchctl</string>
      <string>setenv</string>
      <string>GIT_DEVS_LIST_SECRET</string>
      <string>$GIT_DEVS_LIST_SECRET</string>

      <string>/bin/launchctl</string>
      <string>setenv</string>
      <string>GIT_RZ_SCRIPTS_SECRET</string>
      <string>$GIT_RZ_SCRIPTS_SECRET</string>

    </array>
    <key>RunAtLoad</key>
    <true/>
    <key>ServiceIPC</key>
    <false/>
  </dict>
</plist>
EOF
````

