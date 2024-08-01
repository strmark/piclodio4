[![Java CI with Maven](https://github.com/strmark/piclodio4/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/strmark/piclodio4/actions/workflows/maven.yml)


# Piclodio

This repository was inspired by from https://github.com/Sispheor/piclodio3/.
The original project was written in Python2(django) and Angular 2. I've initial rewritten it in Spring-boot and Angular 7.

Piclodio is a web radio player and a also an alarm clock that can be installed on a Raspberry Pi.
You can add url stream to complete the collection. Scheduling alarm clock is easy and can be periodic.
A local backup MP3 file is used in case of losing internet connection or if the web radio is not anymore available to be sure you'll be awaken.

![piclodio_home](https://github.com/strmark/piclodio4/blob/master/front/images/piclodio_presentation.png)

## Installation

### Manual install
The project is split in two parts:
- [Backend](back/README.md)
- [Frontend](front/README.md)

Installation procedures have been tested on a Raspberry Pi(raspian) and on debian but the project should works on any Linux system that can handle Java and Angular.

## License

Copyright (c) 2019 - 2024. All rights reserved.

Piclodio is covered by the MIT license, a permissive free software license that lets you do anything you want with the source code, as long as you provide back attribution and "don't hold you liable". For the full license text see the [LICENSE.md](LICENSE) file.
