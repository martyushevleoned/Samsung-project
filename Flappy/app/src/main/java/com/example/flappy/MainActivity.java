package com.example.flappy;

//import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/*
 * описание алгоритма
 * класс Wall
 * поля:
 * emptySpace - растояние между трубами
 * height - расстояние от верха холста до низа верхней трубы
 * indent - отступ от верхнего и нижнего краев холста
 * x - координата трубы по х
 * vx - скорость трубы по х
 * downTube - изображение нижней трубы
 * upTube - изображение верхней трубы
 * hTube - высота изображения трубы
 * groundHeight - высота видимой части земли
 * методы:
 * update - отвечает за передвижение труб
 * drawHitBox - отрисовывает хитбокс
 * draw - отрисовывает трубы
 * generate - вызывается в методе update когда труба заходит за край, генерирует новое значение height
 *
 * класс Sprite
 * поля:
 * x. у - координаты
 * vx, vy скорости
 * g - ускорение (действует только по координате у)
 * frameWidth - ширина спрайта
 * frameHeight - высота спрайта
 * currentFrame - номер используемого спрайта
 * frameTime - время которое будет длится каждый кадр анимации
 * методы:
 * update - смена кадров, реализация передвижения
 * drawHitBox - отрисовывает хитбокс
 * draw - отрисовывает птицу
 * */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(new GameView(this));
    }
}