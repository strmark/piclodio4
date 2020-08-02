
# Piclodio backend

This part of the project is written with Spring-boot, H2 database, VLC, ALSA and the Quartz scheduler to provide the backend REST API.

## Installation 
This installation procedure will works on Raspian.

### Pre requisite and libs

``` bash
sudo apt-get update
sudo apt-get install apt-transport-https
sudo apt-get install git openjdk-11-jdk vlc libasound2-dev
```

Download maven from https://maven.apache.org/ and follow the installation instruction within binary tar or zip.

Clone the project
``` bash
git clone https://github.com/strmark/piclodio4.git
```

Make the necessary folders
``` bash
mkdir /home/pi/database
mkdir /home/pi/piclodio
```

Copy the database to /home/pi/database
``` bash
cd back
cp database/piclodio.db.* /home/pi/database/
```

## Run the backend

### VLC player setting
Start the VLC player on the Raspberry Pi and select the Alsa Audio Output as the Output module.

### Manually with mvn
``` bash
cd back
mvn spring-boot:run
```

### Automatically at each startup with systemd (Prod)
``` bash
mvn package
=======
cp target/piclodio-0.0.1-SNAPSHOT.jar /home/pi/piclodio/
```
Create and open a Systemd service file for piclodio with sudo privileges in your text editor:
``` bash
sudo nano /etc/systemd/system/piclodio.service
```

Place the following content (update the WorkingDirectory path depending on your installation)
``` bash
[Unit]
Description=piclodio daemon
After=network.target

[Service]
User=pi
Group=pi
WorkingDirectory=/home/pi/piclodio
ExecStart=/usr/bin/java -jar piclodio-0.0.1-SNAPSHOT.jar

[Install]
WantedBy=multi-user.target
```

We can now start the service we created and enable it so that it starts at boot:
``` bash
sudo systemctl daemon-reload
sudo systemctl start piclodio
sudo systemctl enable piclodio
```

The backend API should now be accessible on the port 8000 of the server.