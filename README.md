# FsExplorer

Простой менеджер файловой системы.

# Общее описание архитектуры

Приложение построено по принципу MVC. Пакет `fs.explorer.views` содержит классы, котрые представляют элементы графического интерфейса (`MenuBar`, `FTPDialog` и т.д.). События элементов GUI (выбор пунктов меню, разворачивание дерева каталогов) обрабатываются соответствующими контроллерами, которые лежат в пакете `fs.explorer.controllers`. Для получения данных о содержании каталогов и архивов, а так же для построения превью файлов, контроллеры пользуются провайдерами из пакета `fs.explorer.providers`. Получив нужную информацию, контроллеры обновляют модели из `fs.explorer.models` и элементы GUI.

# Добавление новых видов превью

За создание превью файлов отвечают классы из пакета `fs.explorer.providers.preview`. `PreviewController` определяет тип файла по расширению и вызывает соответствующий метод у `DefaultPreviewProvider` (`DefaultPreviewProvider.getTextPreview`, `getTextPreview.getImagePreview`). `DefaultPreviewProvider` считывает файл и просит `DefaultPreviewRenderer` сгенерировать превью (`DefaultPreviewRenderer.renderText`, `DefaultPreviewRenderer.renderImage`). Таким образом, чтобы добавить превью для новых типов файлов, нужно добавить соответствующий метод в `DefaultPreviewProvider` и `DefaultPreviewRenderer`, после чего вызвать добавленный метод в `PreviewController`.
