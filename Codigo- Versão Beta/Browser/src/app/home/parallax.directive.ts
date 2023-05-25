import { Directive, Input, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[homeParallax]'
})
export class ParallaxDirective {

  @Input('ratio') parallaxRatio: number = 1
  initialTop: number = 0

  constructor(private eleRef: ElementRef) {
    this.initialTop = this.eleRef.nativeElement.getBoundingClientRect().top
  }

  @HostListener("window:scroll", ["$event"])
  onWindowScroll(event) {
      if(this.parallaxRatio != 0.5){
        this.eleRef.nativeElement.style.top = (this.initialTop - (window.scrollY * this.parallaxRatio)) + 'px'
      }
      //For sun img
      else{
        this.eleRef.nativeElement.style.left = (this.initialTop - (window.scrollY * this.parallaxRatio)) + 'px'
        this.eleRef.nativeElement.style.top = (this.initialTop - (-window.scrollY * this.parallaxRatio)) + 'px';
      }
  }

}