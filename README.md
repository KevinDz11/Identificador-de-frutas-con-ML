# Identificador de Frutas y Verduras con Machine Learning

Este proyecto es una aplicación Android nativa diseñada para identificar diversos tipos de frutas y verduras en tiempo real utilizando la cámara del dispositivo y modelos de Inteligencia Artificial. Además de la identificación, la app ofrece información valiosa sobre conservación, nutrición y costos.

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
