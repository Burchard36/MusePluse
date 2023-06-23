# MusePluse
 MusePluse is an extremely powerful Minecraft music player. Capable of taking YouTube links and automatically converting
them into resource packs & sending them to the players on your server. And when you think that was it, is also
comes with a full fleshed out GUI player for all your players.

MusePluse also comes with A extremely powerful developer API, every feature of the plugin is exposed publicly
meaning any methods I use for developing the plugin are directly available to you! This is still under development & documentation
will need to be done, any questions regarding the API can be directed to any of the developers themselves :)


# Installing & Setting up MusePluse

Read our docs, installing MusePluse has changed since 1.0.0 https://docs.musepluse.com

***WARNING ABOUT CLIENT-SIDE STORAGE SPACE FOR ADMINISTRATORS OR SERVERS THAT CONSISTANTLY ADD SONGS***

Due to current bugs within recent minecraft code I am unable to use hash's for updating the resource pack. This means in order to force an 
update when you regenerate your resource pack we just send a file with a different UUID, meaning minecraft on your client may leave behind a bunch of unused server-sides 
resource packs located in `%appdata%\.minecraft\server-resource-packs`. This is a bug within minecraft itself see here: https://bugs.mojang.com/browse/MC-164316
