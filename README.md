# Identificador de Frutas y Verduras con Machine Learning

Este proyecto es una aplicación Android nativa diseñada para identificar diversos tipos de frutas y algunas verduras en tiempo real utilizando la cámara del dispositivo y modelos de Inteligencia Artificial. Además de la identificación, la app ofrece información valiosa sobre conservación, nutrición y costos.

---

##  Procesamiento y Clasificación

### Clasificación de Imágenes en Tiempo Real
La clasificación no se detiene mientras usas la app. El sistema implementa un flujo de **inferencia asíncrona**:

* **Captura Continua**: La cámara genera una secuencia de fotogramas (*frames*) que se envían al motor de IA sin interrumpir la vista previa del usuario.
* **Interpretación de Tensores**: El `Interpreter` de TensorFlow Lite procesa la imagen convertida en un búfer de datos y genera un mapa de probabilidades para las **62 categorías** configuradas.
* **Filtrado de Resultados**: Para evitar "ruido" en la pantalla, el sistema ordena los resultados por puntaje y solo entrega al usuario la etiqueta con el nivel de confianza más alto.

### CameraX Analysis API
Es el puente entre el hardware de la cámara y el código de Machine Learning:

* **ImageAnalysis Case**: Se configura un caso de uso específico de "Análisis de Imagen" que permite acceder a los buffers de píxeles de la cámara en tiempo real.
* **Gestión de Hilos**: Utiliza un ejecutor (*executor*) separado para que el procesamiento pesado de la IA ocurra en segundo plano, garantizando que la interfaz (UI) se mantenga fluida a 60 FPS.
* **Conversión de Formatos**: Gestiona la compleja conversión de formatos de imagen nativos de Android (YUV_420_888) a `Bitmap`, que es lo que el clasificador requiere.

### TFLite Support Library
Actúa como el "pre-procesador" matemático que normaliza los datos sensoriales antes de entrar al modelo:

* **Pipeline de Transformación**: Utiliza `ImageProcessor` para encadenar operaciones como rotación automática basada en la orientación del sensor (`Rot90Op`).
* **Reescalado Inteligente**: Implementa `ResizeOp` para ajustar cualquier resolución de cámara al tamaño exacto de entrada del modelo (**224x224 píxeles**) usando interpolación bilineal.
* **Normalización de Datos**: Convierte los valores de color de los píxeles (0-255) al tipo de dato `FLOAT32` requerido por la arquitectura *Efficient_v3* del modelo.

---

##  Gestión de Datos e Información

### Base de Datos Inteligente
La aplicación utiliza un sistema de **mapeo semántico** para enriquecer la simple etiqueta de la IA:

* **Almacenamiento Descentralizado**: La información detallada no vive en el modelo de IA, sino en un archivo `frutas_datos.json` optimizado para consultas rápidas.
* **Mapeo de Etiquetas**: Al detectar una etiqueta (ej. "tomato"), el `FrutaRepository` busca esa clave exacta en el JSON para extraer consejos de almacenamiento, vida útil e índice glucémico.
* **Extensibilidad**: Este diseño permite actualizar la base de conocimientos o los consejos nutricionales sin necesidad de volver a entrenar el modelo de Machine Learning.

### Estimación de Costos
Los datos económicos integrados en el sistema sirven como una referencia de mercado para el usuario:

* **Origen de los Datos**: Los precios reflejados en `frutas_datos.json` (ej. $28.50 para tomate, $22.00 para banana) son valores de referencia basados en promedios de mercado para ayudar en la planificación del presupuesto.
* **Unidad de Medida**: Los valores representan un costo estimado por unidad o kilogramo según el estándar de comercialización del producto.

---

##  Model / Data Layer
Es el cimiento de la arquitectura MVVM, encargada de la persistencia y el procesamiento de bajo nivel:

* **FrutaRepository**: Centraliza el acceso a los datos. Lee el archivo JSON desde la carpeta de *assets* y lo convierte en objetos de Kotlin (`FrutaInfo`) para su uso en la interfaz.
* **ImageClassifierHelper**: Encapsula la lógica de TensorFlow Lite. Su función es aislar la complejidad de cargar el archivo `.tflite`, configurar los hilos del CPU (*threads*) y ejecutar la inferencia.
* **Data Classes**: Define estructuras de datos estrictas que aseguran que la información (precio, superpoder, tips) se maneje de forma segura y sin errores en toda la aplicación.

![11](https://github.com/user-attachments/assets/075abe31-78b3-49c1-9836-f4e46d6e0f6b)
![111](https://github.com/user-attachments/assets/a79897da-3ea4-4c1b-bf1b-f912d4a6df30)
![1111](https://github.com/user-attachments/assets/921d892a-b24e-4f97-b33c-429d54b98f94)

---

## Recursos

Material complementario para visualizar el funcionamiento y la presentación del proyecto:

* **Video Tutorial**: https://youtu.be/5GjMJrkjf7g
* **Presentación**: https://www.canva.com/design/DAG9lyLCIic/v08dWfKcLt4oOxUhw18eyg/view?utm_content=DAG9lyLCIic&utm_camp

---

##  Conclusiones

Este proyecto fue desarrollado colaborativamente por:
1. **Beltrán Vidal Sol Jarelly**
2. **De la Vega Marquez Anuar**
3. **Diaz Fuentes Kevin**
* integrando diversas áreas de la ingeniería de software y la inteligencia artificial. Las principales conclusiones de nuestro desarrollo son:

1. **Sinergia entre ML y Desarrollo Móvil**: Logramos demostrar que la integración de modelos complejos como *Efficient_v3* en dispositivos móviles es viable y eficiente gracias a herramientas como **TFLite Support Library**, permitiendo experiencias de usuario fluidas sin dependencia de la nube.
2. **Arquitectura Escalable**: La implementación del patrón **MVVM** facilitó la división de tareas entre los 3 integrantes, permitiendo trabajar simultáneamente en la lógica de la cámara, el procesamiento de datos JSON y la interfaz en Jetpack Compose sin conflictos de código.
3. **Impacto en el Usuario**: Más allá de la clasificación técnica, el valor real del proyecto reside en la democratización del conocimiento nutricional y de conservación, transformando un simple identificador de imágenes en una herramienta de consumo responsable.
4. **Optimización de Recursos**: El uso de **CameraX Analysis API** fue fundamental para equilibrar el rendimiento del hardware; logramos un análisis constante de frames manteniendo la estabilidad térmica y de batería del dispositivo.

