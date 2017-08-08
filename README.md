# FsExplorer

Простой менеджер файловой системы.

# Общее описание архитектуры

Приложение построено по принципу MVC. Пакет `fs.explorer.views` содержит классы, котрые представляют элементы графического интерфейса (`MenuBar`, `FTPDialog` и т.д.). События элементов GUI (выбор пунктов меню, разворачивание дерева каталогов) обрабатываются соответствующими контроллерами, которые лежат в пакете `fs.explorer.controllers`. Для получения данных о содержании каталогов и архивов, а так же для построения превью файлов, контроллеры пользуются провайдерами из пакета `fs.explorer.providers`. Получив нужную информацию, контроллеры обновляют модели из `fs.explorer.models` и элементы GUI.

# Добавление новых видов превью

За создание превью файлов отвечают классы из пакета `fs.explorer.providers.preview`. `PreviewController` определяет тип файла по расширению и вызывает соответствующий метод у `DefaultPreviewProvider` (`DefaultPreviewProvider.getTextPreview`, `getTextPreview.getImagePreview`). `DefaultPreviewProvider` считывает файл и просит `DefaultPreviewRenderer` сгенерировать превью (`DefaultPreviewRenderer.renderText`, `DefaultPreviewRenderer.renderImage`). Таким образом, чтобы добавить превью для новых типов файлов, нужно добавить соответствующий метод в `DefaultPreviewProvider` и `DefaultPreviewRenderer`, после чего вызвать добавленный метод в `PreviewController`.

# Ограничения

1. Автоматическое обновление содержимого папок и архивов не поддерживается.
2. Ручное обновление содержимого папок доступно через меню `Selected -> Reload`. Ручное обновление для содержимого архивов не поддерживается.
3. При построении превью для картинок, приложение не умеет определять, что изображение испорчено или имеет неверный формат. Вместо испорченного изображения просто показывается пустое превью. Превью для изображений строится с помощью `javax.swing.ImageIcon`. Этот класс, судя по всему, предоставляет очень ограниченные возможности по определению испорченности формата изображения. Вероятно, для корректной обработки лучше использовать внешнюю библиотеку для загрузки изображений.
4. При построении превью текста используется стандартная кодировка, возможности выбрать другую кодировку нет (это довольно легко исправить).
5. При чтении содержимого архивов используется стандартная кодировка, возможности выбрать другую кодировку нет (тоже легко исправить). В результате, названия папок и файлов внутри архивов могут быть некорректными.
6. При чтении некорректных архивов приложение отображает их содержимое как пустое, а не сообщает об ошибке. Для чтения используется класс `java.util.zip.ZipInputStream`, который не умеет определять, что архив испорчен, он просто говорит, что в нем нет записей.
