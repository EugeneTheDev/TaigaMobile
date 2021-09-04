## Tools
It is recommended to use latest version of Android Studio

## Tests
In order to run tests you need to setup new clean Taiga instance first. I recommend running it locally in Docker.  
You can grab repository for Docker [here](https://github.com/kaleidos-ventures/taiga-docker/) and then just follow instructions.  
And do not forget to enable public register (in `docker-compose.yml`):
```dockerfile
taiga-back:
    image: taigaio/taiga-back:latest
    environment:
      <<: *default-back-environment
      PUBLIC_REGISTER_ENABLED: "True"
```
