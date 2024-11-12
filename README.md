# EnigmaIRC Paper
EnigmaIRC integration for Minecraft.

## What is it
EnigmaIRC Paper is a Minecraft server plugin that allows players to communicate with messenger users.
This plugin combines both the server side of the messenger (for receiving and forwarding messages) and the client side (for sending messages from the game).

### Requirements
Minimum host requirements:
- Minecraft server on Paper *(or its forks)*
- 1 GB RAM
- 2 CPU cores
- 2 free ports - game and irc.

## Installation and setup
Download the plugin from the latest release page. Place it in the `/plugins` folder and start the server.

The plugin will automatically generate a config and a new encryption key.

Configure the config, which is located in the `/plugins/eirc-server/config.yml` file. Follow the comments provided by default.

After configuring the plugin, reboot the server to apply the changes. Next, share the encryption key and address with your friends.

To communicate with players on the server, download one of the following client options:
- [Desktop](https://github.com/Dertfin3051/EnigmaIRC-Java) (Java/multiplatform)
- [Android](https://github.com/eirc-fork/EnigmaIRC-Mobile) 8+

Then configure it according to the instructions. Specify the same encryption key on all clients as is installed on the server. As an address, specify the public address of the Minecraft server <u>without the port</u>, and specify the port <u>that is specified in config.yml</u>

## Chat and formatting
To change the type of messages that are sent to the EnigmaIRC client on the Minecraft server, edit the `message-format` and `status-format` parameters in the config. To make colored/stylized text, use the [MiniMessage by Kyori](https://docs.advntr.dev/minimessage/format.html) format.

To easily create a MiniMessage, use the [MiniMessage Viewer](https://webui.advntr.dev/)

MiniMessage example:
```yaml
message-format : "<dark_gray>[EIRC]</dark_gray> <user> <gray>-</gray> <message>"
```