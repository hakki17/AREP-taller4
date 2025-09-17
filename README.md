# AREP-taller4

## Resumen del Proyecto

Este proyecto implementa una aplicación web usando un framework personalizado llamado **MicroSpringBoot** (en lugar de Spring Boot tradicional). La aplicación se containeriza con Docker y se despliega en AWS EC2. El framework personalizado utiliza anotaciones propias (`@RestController`, `@GetMapping`, `@RequestParam`) para crear endpoints REST de manera similar a Spring Boot pero con una implementación completamente personalizada.

## Arquitectura

La aplicación sigue una arquitectura de microservicios simple con los siguientes componentes:

### Componentes Principales:

1. **HttpServer**: Servidor HTTP personalizado que maneja peticiones concurrentes
2. **Controladores**: Clases anotadas con `@RestController` que definen endpoints
3. **Sistema de Anotaciones**: Framework de anotaciones personalizado para mapeo de rutas
4. **Contenedor Docker**: Encapsula la aplicación para despliegue
5. **AWS EC2**: Infraestructura de despliegue en la nube

### Flujo de Arquitectura:

```
Cliente → AWS Load Balancer → EC2 Instance → Docker Container → HttpServer → Controlador → Respuesta
```

## Diseño de Clases

### Estructura del Proyecto

![Estructura del Proyecto](https://github.com/hakki17/AREP-taller4/blob/main/img/1.%20tree.png)

### Clases Principales:

#### 1. **HttpServer**
- **Responsabilidad**: Manejo de peticiones HTTP, enrutamiento y servicios estáticos
- **Características**: 
  - Concurrencia mediante ExecutorService
  - Apagado elegante con shutdown hooks
  - Carga automática de servicios anotados
  - Soporte para archivos estáticos

#### 2. **Controladores**
- **HelloController**: Maneja endpoints de saludo y demostración
- **Métodos**:
  - `greeting()`: Endpoint personalizable con parámetros
  - `pi()`: Retorna el valor de PI
  - `helloworld()`: Mensaje simple de bienvenida

#### 3. **Sistema de Anotaciones**
- **@RestController**
- **@GetMapping**
- **@RequestParam**

#### 4. **Clases de Utilidad**
- **HttpRequest**
- **HttpResponse**
- **Service**

## Generación de Imágenes Docker

### Dockerfile

```dockerfile
FROM openjdk:17
 
WORKDIR /usrapp/bin
 
ENV PORT=8080
 
COPY /target/classes /usrapp/bin/classes
COPY /target/dependency /usrapp/bin/dependency
COPY /webroot ./webroot
 
CMD ["java","-cp","./classes:./dependency/*","co.escuelaing.arep.microspringboot.MicroSpringBoot"]
```

### Comandos para Construcción

```bash
# Compilar proyecto
mvn clean package

# Construir imagen Docker
docker build --tag hakki17/arep-taller4:latest .

# Verificar imagen creada
docker images
```

![Docker Images](https://github.com/hakki17/AREP-taller4/blob/main/img/9.imagenes.png)

### Ejecución Local de Contenedores

```bash
# Ejecutar múltiples instancias
docker run -d -p 34000:8080 --name container1 hakki17/arep-taller4:latest
docker run -d -p 34001:8080 --name container2 hakki17/arep-taller4:latest  
docker run -d -p 34002:8080 --name container3 hakki17/arep-taller4:latest
```

### Verificación de Contenedores

![Docker PS](https://github.com/hakki17/AREP-taller4/blob/main/img/4.%20docker%20ps.png)

![Docker Desktop](https://github.com/hakki17/AREP-taller4/blob/main/img/5.%20docker%20desktop.png)

### Pruebas Locales

**Puerto 34000:**
![Prueba Puerto 34000](https://github.com/hakki17/AREP-taller4/blob/main/img/3.1%2034000.png)

**Puerto 34001:**
![Prueba Puerto 34001](https://github.com/hakki17/AREP-taller4/blob/main/img/3.2%2034001.png)

**Puerto 34002:**
![Prueba Puerto 34002](https://github.com/hakki17/AREP-taller4/blob/main/img/3.3%2034002.png)

## Docker Hub

### Repositorio

La imagen se encuentra disponible en Docker Hub:

![Docker Hub Repo](https://github.com/hakki17/AREP-taller4/blob/main/img/8.%20repo%20en%20docker%20hub.png)

![Docker Hub Details](https://github.com/hakki17/AREP-taller4/blob/main/img/8.1%20repo%20docker%20hub.png)

### Comandos de Publicación

```bash
# Etiquetar imagen
docker tag hakki17/arep-taller4:latest hakki17/arep-taller4:latest

# Autenticarse en Docker Hub
docker login

# Subir imagen
docker push hakki17/arep-taller4:latest
```

## Despliegue en AWS

### Infraestructura

Se utilizó una instancia EC2 existente de la clase anterior:

![Máquina Virtual AWS](https://github.com/hakki17/AREP-taller4/blob/main/img/6.%20MV%20AWS.png)

- **IP Pública**: 3.87.25.160
- **Security Group**: Configurado para puertos 22 (SSH), 80 (HTTP), y 42000 (aplicación)

### Proceso de Despliegue

```bash
# Conectarse a la instancia EC2
# (Usando EC2 Instance Connect desde la consola AWS)

# Descargar imagen desde Docker Hub
docker pull hakki17/arep-taller4:latest

# Ejecutar contenedor en AWS
docker run -d -p 42000:8080 --name microspringboot-aws hakki17/arep-taller4:latest

# Verificar despliegue
docker ps
```

### Configuración de Red

Se configuró el Security Group para permitir tráfico en el puerto 42000:
- **Tipo**: Custom TCP
- **Puerto**: 42000
- **Origen**: 0.0.0.0/0 (cualquier IP)

## Pruebas del Despliegue

### Endpoints Funcionales

**Endpoint Greeting:**
![Despliegue Greeting](https://github.com/hakki17/AREP-taller4/blob/main/img/10.%20despliegue%20greeting.png)
- URL: `http://ec2-3-87-25-160.compute-1.amazonaws.com:42000/app/greeting?name=AWS`
- Respuesta: "Hello AWS"

**Endpoint Pi:**
![Despliegue Pi](https://github.com/hakki17/AREP-taller4/blob/main/img/11.%20despliegue%20pi.png)
- URL: `http://ec2-3-87-25-160.compute-1.amazonaws.com:42000/app/pi`
- Respuesta: Valor de PI (3.141592653589793)

**Endpoint Hello World:**
![Despliegue Hello World](https://github.com/hakki17/AREP-taller4/blob/main/img/12.%20despliegue%20helloWorld.png)
- URL: `http://ec2-3-87-25-160.compute-1.amazonaws.com:42000/app/helloworld`
- Respuesta: "Hello World"

## Características Implementadas

### Concurrencia

### Apagado Elegante

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    System.out.println("\nIniciando apagado elegante...");
    shutdown();
}));
```


### Framework Personalizado

- **Sin dependencias de Spring Boot**
- Sistema de anotaciones propio
- Carga automática de controladores
- Enrutamiento dinámico
- Manejo de parámetros de query

## Video Demostración

Para ver la demostración completa del funcionamiento:

## 

[![VIDEO PRUEBAS](https://github.com/hakki17/AREP-taller4/blob/main/img/13.png)](https://www.youtube.com/watch?v=axNdCg0jThQ)

## Instrucciones de Ejecución

### Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- Docker
- Cuenta en Docker Hub
- Instancia AWS EC2

### Ejecución Local

```bash
# Clonar repositorio
git clone https://github.com/hakki17/AREP-taller4/tree/main

# Compilar
mvn clean package

# Ejecutar localmente
java -cp "target/classes:target/dependency/*" co.escuelaing.arep.microspringboot.MicroSpringBoot
```

### Ejecución con Docker

```bash
# Construir imagen
docker build --tag tu-usuario/arep-taller4 .

# Ejecutar contenedor
docker run -d -p 8080:8080 tu-usuario/arep-taller4
```

### Despliegue en AWS

```bash
# En la instancia EC2
docker pull hakki17/arep-taller4:latest
docker run -d -p 42000:8080 --name app hakki17/arep-taller4:latest
```

## Conclusiones

Este proyecto demuestra exitosamente:

1. **Desarrollo de framework personalizado** que replica funcionalidades de Spring Boot
2. **Implementación de concurrencia** y apagado elegante en servidores HTTP
3. **Containerización efectiva** con Docker
4. **Despliegue en la nube** usando AWS EC2
5. **Gestión de imágenes** con Docker Hub

El uso de un framework personalizado en lugar de Spring Boot cumple con los requisitos de la tarea, demostrando comprensión profunda de los conceptos de desarrollo web, containerización y despliegue en la nube.

## Autor

María Paula Sánchez Macías- Estudiante Ingeniería de sistemas, Escuela Colombiana de Ingenieria Julio Garavito

## Tecnologías Utilizadas

- Java 17
- Maven
- Docker
- AWS EC2
- Docker Hub
- Framework personalizado MicroSpringBoot