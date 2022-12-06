# Name Project: "Садовый земельный участок на Android"
____
***Дисциплины:*** *"Программирование мобильных устройств и встраиваемых систем" & "Программирование интернет-приложений"*

***Команда разработки:*** *студенты группы РИС-19-1б Люкина Диана, Пермякова Полина*

## Введение
Программа "Садовый земельный участок" - это конструктор карт земельных участков. Предназначена для сохранения данных о расположении садовых культур на карте.

## Основания для разработки
Защита полученных знаний по дисциплинам "Программирование мобильных устройств и встраиваемых систем" и "Программирование интернет-приложений"

## Требования к программе или программному изделию
Основными задачами приложения являются:
- индивидуальная настройка участков по площади
- возможность расположения объектов (в данном проекте ими являются рассады и различные садовые культуры) на созданном участке
- сохранение разметки участка с её объектами для будущего использования

База данных должна содержать данные созданных участков, садовые культуры

Дополнительно, программа должна иметь доступ в интернет: получать информацию о погоде, сортах и отправлять данные об участках другим лицам в соцсети по желанию пользователя. 

Программа должна быть реализована в операционной системе Android.

В первой версии продукт будет нацелен для индивидуальной работы пользователя. Общий доступ к одной карте не рассматривается.

--- 

### Ссылка на интерфейс
[Ссылка на Figma](https://www.figma.com/file/WTgZHccmoqRfi1nr9tXf8D/Garden-Application?node-id=0%3A1&t=lTV5qti7NxaPDrvz-0) с интерфейсом мобильного приложения

### Физическая модель базы данных
![Модель базы данных](/img/%D0%94%D0%B8%D0%B0%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B0%20%D0%B1%D0%B5%D0%B7%20%D0%BD%D0%B0%D0%B7%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F.drawio.png)

### Что сделано
![Сделанная часть на момент 6.12.22](/img/made.png)

* Как видно из фотографии, запрограммирован переход из главного экрана в экран с добавлением нового участка. 
* На этих экранах настроен интерфейс, но не совсем стилилизовован
* При нажатии кнопки сохранения (дискета) на экране с добавлением участка данных сохраняются в базу данных SQLite. Новые данные выводятся списком на главном экране, как и на фотографии. 
* В __базе данных__ сделана только таблица с информацией об участках

### ***Огромный список задач))***
Буду расписывать по экранам
* __Построение участка(главный экран)__

1. Меню (троеточие вверху) - загрузка нового участка, файл с которым нам пришлется через соц сеть
2. При нажатии какого-либо из участков мы должны уже перейти на экран с картой, а не на экран с "добавлением"
3. Получение информации о погоде из интернета и её выводят

* __Добавление нового участка__
1. Меню - сделать так, чтобы при добавлении нового участка в меню была только дискета, а приредактировании участка к иконке сохранения добавлялось троеточие с возможностью удалить этот участок из базы данных 
2. После сохранения необходимо, чтобы экран сразу переключался на экран с картой самого участка

* __Карта участка__
1. Создать экран
2. Отрисовка карты - пока сделаем простой, в виде прямоугольника. Лучше сделать, конечно, какое то масштабирование - расположение деревьев в зависимости от площади участка: если площадь большая, то изображение дерева меньше, если маленькая, то деревья соответственно больше, тем самым показывая, сколько свободного места есть от деревьев.
3. Кнопка с добавлением дерева - картинка с деревом просто появляется на экране.
4. При нажатии картинки дерева должны появляться кнопки 
    * с информацией о дереве (это же переход в другой экран с редактированием информации о дереве, котороя первоначально пустая)
    * закрепить дерево от позиции экрана или открепить, чтобы не было случайного передвижения 
    * удалить дерево
5. Меню 
    * поделиться участком 
    * троеточие: 
        * переход к редактированию информации об участке
        * переход на главный экран

* __Информация о дереве__
1. Создать экран
2. Получение информации о дереве и сорте из википедии и её вывод 
3. Меню - сохранение данных и удаление дерева из базы, а значит удаление картинки этого дерева и из карты

* __База данных__
1. Дополнить базу данных таблицей с информацией о дереве

