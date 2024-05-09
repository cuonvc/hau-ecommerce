## Hau-Ecommerce
- Backend module for the final project of the Ecommerce System Development course
- [App mobile source](https://github.com/yosefPham/ecommerce-app/tree/dev_local)
#### 20CN2 - Hanoi Architectural University

### Application architecture diagram
![alt text](System-design.png)
### How to pull and start the backend module

#### 1. Install Docker and Docker-compose
- [On Ubuntu](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-20-04)
- [On Windows](https://docs.docker.com/desktop/install/windows-install/#install-docker-desktop-on-windows)
#### 2. Pulling and starting the backend service
- Include the logs to monitoring
```
docker-compose up
```
- Or do not include the logs
```
docker-compose up -d
```
...
