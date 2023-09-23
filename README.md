# PixeleCraft

A minecraft java edition server

<hr>

Written by the CraftSuperLoaderTeam and PiexleCraftBBS , based on the source code of SpigotMC and MCP, self-developed Minecraft server, this project truly implements a server that is not based on any open source core.
At present, the server abandons the original version of single-threaded processing and changes to multi-threaded processing, which can better utilize multiple cores of the CPU.

# Library

We use this libraries.

* Netty `4.0.23.Final` Used for network communication
* jopt-simple `5.0.4` Used for command line argument parsing
* gson `2.8.0` Used for JSON serialization and deserialization
* snakeyaml `1.19` Used for the parsing of YAML files
* authlib `1.5.21` Used to obtain Minecraft account information
* jline `3.23.0` Used for console input reader.

# API

We are sorry, But PiexleCraft can't use Bukkit plugin. You can devlopment PiexleCraft plugin for this server.

Interface document at [Document](https://craftsuperloaderteam.github.io/PiexleCraftDoc)

# Build

We use maven to handle our dependencies.

* Install [Maven3](http://maven.apache.org/download.html)
* Check out this repo and: `mvn clean install`

and you can use IntelliJ IDEA import this project

* Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
* Launch IDE
