
# Piclodio backend

This part of the project is written with Spring-boot, H2 database, MPlayer, ALSA and the Quartz scheduler to provide the backend REST API.

## Installation 
This installation procedure will works on Raspian.

### Pre requisite and libs

Clone the project
```bash
git clone https://github.com/strmark/piclodio4.git
```

Make the necessary folders
```bash
mkdir /home/pi/database
mkdir /home/pi/piclodio
```

Copy the database to /home/pi/database
```bash
cd back
cp database/piclodio.db.* /home/pi/database/
```

## Run the backend

### MPlayer setting
I make use of an USB audo device with the Raspberry Pi. For that I adjusted the config file of the mplayer. An example of my config file can be found in back/config.

### manually with the integrated web server
### Manually with mvnw
```bash
cd back
./mvnw spring-boot:run
```

### Automatically at each startup with systemd (Prod)
```bash
./mvnw package
cp target/piclodio-0.0.1-SNAPSHOT.jar /home/pi/piclodio/
```
Create and open a Systemd service file for Gunicorn with sudo privileges in your text editor:
```bash
sudo nano /etc/systemd/system/piclodio.service
```

Place the following content (update the WorkingDirectory path depending on your installation)
```bash
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

We can now start the Gunicorn service we created and enable it so that it starts at boot:
```bash
sudo systemctl daemon-reload
sudo systemctl start piclodio
sudo systemctl enable piclodio
```

The backend API should now be accessible on the port 8000 of the server.

