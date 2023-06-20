# MusePluse
 MusePluse is an extremely powerful Minecraft music player. Capable of taking YouTube links and automatically converting
them into resource packs & sending them to the players on your server. And when you think that was it, is also
comes with a full fleshed out GUI player for all your players.

MusePluse also comes with A extremely powerful developer API, every feature of the plugin is exposed publicly
meaning any methods I use for developing the plugin are directly available to you! This is still under development & documentation
will need to be done, any questions regarding the API can be directed to any of the developers themselves :)


# Installing & Setting up MusePluse

Installing muse pluse is just as simple as any other plugin, just drag and drop it into your plugins folder.
However, MusePluse will need a few steps of configuration before it can work properly:

### Using the integrated server to serve the resource pack (Default & Recommended)

1) You need to do is make sure that port `6969` is open on your server! The port needs to be open
because your server will be the one asynchronously sending the resource pack. This port **may** be changed in `settings.yml`,
However **DO NOT** make the port longer than **4 Numbers** or your clients won't be able to download your resource pack!

2) Next, you will need to set your server IP in `the settings.yml`, this will be the IP/Domain you use to connect to the server, this should 
**ONLY** be the IP/Domain of your server and not include any `ports`,`https://`, so on and so forth. For Example: `123.56.123.45` or `my.awsome.server`
are **valid** but `https://your.awesome.server` or `123.23.123.23:25567` are **not**

Once both are set, simply restart your server!

### Hosting the resource pack on an external website

1) For this, ensure that you **DISABLE** the `ResourcePackServer` in the `settings.yml` (Set its `Enabled` to false)

After this, simply set the link you want in `settings.yml`s `ResourcePack` field. This must be a **direct** download link
to the resource pack, more instructions for this part will be included later.

## Your first launch

Your first launch may be very scary and intimidating due to the amount of logging MusePluse does, but do not be afraid!
The plugin will not do this every launch, it is simply downloading the YouTube m4a's from songs.yml and compressing them
into a resource pack for you to use.

If this is your **VERY FIRST** launch, MusePluse will also install FFMPEG (It's how the m4a files get converted to ogg), this may take
a significant amount of time. Do not fear though, this entire process is async.

And as a future note you **are able** to play on the server while this entire process is happening without any issues!

# Adding & Removing your own songs!

For this, you will pay attention to the `songs.yml`, here is an example entry

```yml
# The song key used in things like Player#playSound methods and the /playsound command
# I recommend to just keep this one work, but underscores should be fine
skaga:
  Details:
    # Song name, supports color codes, displayed in /songgui
    SongName: Skaga
    # Artist name, supports color codes, displayed in /songgui
    Artist: Alexander Nakarada
    # This can be any material, it is whats used in /songgui
    # Music discs automatically have their default gray lore removed
    GuiMaterial: MUSIC_DISC_13
    # Permission is an optional field, no permission = no auto queue or access to it in /songgui
    #Permission: "musepluse.skaga"
  # Currently only youtube is supported
  YouTubeLink: https://www.youtube.com/watch?v=Wyx_XYITisE
```

***WARNING ABOUT CLIENT-SIDE STORAGE SPACE FOR ADMINISTRATORS OR SERVERS THAT CONSISTANTLY ADD SONGS***

Due to current limitations/bugs within recent minecraft code I am unable to use hash's for updating the resource pack
for various un-known reasons. This means in order to force an update when you re-generate your resource pack we just send a file with
a different UUID, meaning minecraft on your client may leave behind a bunch of unused server-sides resource packs located in
`%appdata%\.minecraft\server-resource-packs`. This is a limitation I will work ASAP to fix.
