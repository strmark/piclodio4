# Piclodio frontend

This part of the project was written with Angular 7 and was generated with [angular-cli](https://github.com/angular/angular-cli) version 7.3.5. It is now updated to Angular 18.

## Installation

### Pre requisite

Install nodejs 18x 
``` bash
curl -sL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
node --version
```
Clone the project
``` bash
git clone https://github.com/strmark/piclodio4.git
```

Install Angular cli
``` bash
sudo npm install -g @angular/cli
```

Install dependencies
``` bash
cd piclodio4/front/
npm install --legacy-peer-deps
```

If your Pi runs out of memory export the following NODE_OPTION
``` bash
export NODE_OPTIONS="--max-old-space-size=1024"
```

### Run a development server

Run the development server (for testing purposes). If you want to install the frontend continue with the next step Install nginx web server)
``` bash
ng serve
```
Navigate to `http://server_ip:4200/`. The app will automatically reload if you change any of the source files.

### Run a prod server

Install nginx web server
``` bash
sudo apt-get install nginx
```

Build the project to generate static files
``` bash
ng build --configuration production --aot
```

The last command wil generate a "dist" folder. Place it in the nginx web server and give all right to the nginx user
``` bash
sudo cp -R dist/piclodio /var/www/piclodio
sudo chown -R www-data: /var/www/piclodio
```

``` bash
Edit the file default.conf `sudo nano /etc/nginx/sites-available/default` and change the line
        root /var/www/html;
with the following content
        root /var/www/piclodio;

And the line
    location / {
        try_files $uri $uri/ =404;
    }

with the following
    location / {
        try_files $uri $uri/ /index.html;
    }
```
Piclodio is now available from the address IP of your Raspberry Pi.
