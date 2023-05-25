import { Component, Input } from "@angular/core";
import { trigger, transition, style, animate, useAnimation } from "@angular/animations";
import {
  fadeIn,
  fadeOut,
  scaleIn,
  scaleOut,
} from "./carousel.animations";

@Component({
  selector: 'carousel',
  templateUrl: './carousel.component.html',
  styleUrls: ['./carousel.component.scss'],
  animations: [
    trigger("carouselAnimation", [
      //Animacao sem zoom
      transition("void => *", [useAnimation(fadeIn, {params: { time: '1300ms' }} )]),
      transition("* => void", [useAnimation(fadeOut, {params: { time: '1300ms' }})]),
      //Animação com zoom
      //transition("void => *", [useAnimation(scaleIn, {params: { time: '500ms' }} )]),
      //transition("* => void", [useAnimation(scaleOut, {params: { time: '500ms' }})]),
    ])
  ]
})
export class CarouselComponent {

  @Input() slides;

  currentSlide = 0;

  constructor() { }

  onPreviousClick() {
    const previous = this.currentSlide - 1;
    this.currentSlide = previous < 0 ? this.slides.length - 1 : previous;
    console.log("previous clicked, new current slide is: ", this.currentSlide);
  }

  onNextClick() {
    const next = this.currentSlide + 1;
    this.currentSlide = next === this.slides.length ? 0 : next;
    console.log("next clicked, new current slide is: ", this.currentSlide);
  }  
}
