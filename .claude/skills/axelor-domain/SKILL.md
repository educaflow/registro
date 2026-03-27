---
name: axelor-domain
description: 'Modelos de dominio específicos para aplicaciones basadas en Axelor, siguiendo las mejores prácticas para el diseño de modelos de dominio y la lógica de negocio en el contexto de Axelor.'
---

## Modelos de dominio en Axelor

- **Documentación**: La documentación de los modelos de dominio en Axelor se encuentra en el archivo'./references/axelor-models-8.0.md'. Aquí se describen los modelos de dominio específicos para aplicaciones basadas en Axelor, incluyendo sus atributos, relaciones y comportamientos.
- **Modelos definidos**: En la carpeta `./references/entities` se definen los modelos necesarios para el desarrollo de la aplicación.
- **Mapeo de modelos de dominio de Axelor a tablas**: Los modelos de Axelor se convierten en tablas en la base de datos. Cada modelo de dominio se mapea a una tabla, y los atributos del modelo se mapean a columnas en esa tabla. Esto permite que los datos de los modelos de dominio se almacenen y gestionen eficientemente en la base de datos, facilitando las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) y las consultas necesarias para la lógica de negocio de la aplicación.
- **Modelo User**: El modelo definido en `./references/entities/User.xml` extiende del modelo `./references/entities/User-axelor.xml`. Esto significa que el modelo `User` hereda los atributos definidos en `User-axelor`y añade/reemplaza atributos específicos para la aplicación.
- **Nombre de tablas** : El nombre de las tablas en la base de datos es modelo.nombre + entity.name todo en minúsculas. Los espacios se reemplazan por guiones bajos. Por ejemplo:
```xml
<module name="common" package="com.educaflow.subsystem.common.db"/>
    <entity name="Centro">
```
Se mapea a una tabla llamada `common_centro` en la base de datos.

- **Identificadores**: Los modelos de dominio en Axelor utilizan identificadores únicos para cada instancia. Estos identificadores se generan automáticamente y se utilizan para referenciar y gestionar las entidades en la base de datos.
