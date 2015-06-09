# google-drive-client
## Foreword
This application aims to provide a simple way to synchronize your google drive account into a local folder.
It uses the Google Drive JAVA API

At the moment, I don't publish any binary, because I don't know yet how to restrict the number of connections to google apis.

# License
Except the use of GOOGLE DRIVE JAVA API which is licensed under Apache License, this program is distributed under the GPLv3 License.

# API Key


## How to build
### API Keys
This program uses google java api. You have [to enable the drive API and follow instructions from this page] (https://developers.google.com/drive/web/quickstart/java#step_1_enable_the_drive_api)

### Build
This program uses maven 3.2.x

First you have to create a profile in your settings.xml file
```
<profile>
            <id>google-drive-client</id>
            <properties>
                <google.client.id>CLIENT ID </google.client.id>
                <google.client.secret>CLIENT_SECRET</google.client.secret>
                <google.redirect.url>urn:ietf:wg:oauth:2.0:oob</google.redirect.url>
            </properties>
        </profile>
```
then run
```
$ mvn clean install assembly:assembly -P google-drive-client
```