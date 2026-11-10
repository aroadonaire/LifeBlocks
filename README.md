# LifeBlocks

**LifeBlocks** es una aplicación Android de organización personal desarrollada para ayudar a convertir objetivos personales en tareas diarias.

El proyecto nace de la necesidad de organizar las actividades del día a día, establecer pequeñas metas y llevar un seguimiento del progreso para mantener la constancia y avanzar hacia objetivos a largo plazo.

## Tecnologías utilizadas

- **Kotlin** – Lenguaje de programación.
- **Jetpack Compose** – Desarrollo de la interfaz de usuario.
- **Room** – Persistencia y gestión de datos locales.

## Funcionalidades

LifeBlocks está diseñada para facilitar la organización personal y el seguimiento de objetivos.

La aplicación cuenta con diferentes apartados:

Bloques: organización de los objetivos personales en diferentes áreas.

Misiones: gestión de las tareas y actividades que ayudan a alcanzar los objetivos.

Historial: seguimiento de la actividad realizada.

Biblioteca: espacio para gestionar información o elementos relacionados con la organización personal.


## Estructura del proyecto

El proyecto sigue una organización por paquetes y responsabilidades para separar la interfaz de usuario, la gestión de datos y la lógica de la aplicación.

data: contiene las entidades, DAO y la configuración de la base de datos local mediante Room.

ui: contiene los componentes visuales y las pantallas de la aplicación.

blocks: interfaz relacionada con los bloques.

history: pantallas del historial.

library: interfaz de la biblioteca.

navigation: navegación entre las diferentes secciones.

theme: configuración visual y temas de la aplicación.

viewmodel: contiene los ViewModels encargados de gestionar los datos y comunicarlos a la interfaz.

MainActivity: punto de entrada de la aplicación.

## Instalación y ejecución

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/aroadonaire/LifeBlocks.git
   ```
2. Abrir el proyecto con Android Studio.
3. Esperar a que Gradle sincronice las dependencias.
4. Ejecutar la aplicación en un emulador o dispositivo Android compatible.

## Autor

Proyecto desarrollado como aplicación personal para desarrollar mis conocimientos.
