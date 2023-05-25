import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'image-home',
    styleUrls: ['./home.component.css'],
    template: `
    <div class="subComp">
    
    <carousel [slides]="slides"></carousel> 
  </div >
    `
  })
  export class ImageComponent  {
    constructor(public translate: TranslateService){

    }
    public slides = [
      { src: "https://storage.cloud.google.com/apdc-geoproj.appspot.com/1024.jpg" },
      { src: "https://s1.1zoom.me/big0/324/USA_Coast_Oregon_coast_sea_Crag_Trees_576509_1280x791.jpg" },
      { src: "https://s1.1zoom.me/big0/205/Greece_Sunrises_and_sunsets_Coast_Korfu_Crag_Rays_575551_1280x853.jpg" },
      { src: "https://s1.1zoom.me/big0/307/Forests_Autumn_Trees_Rays_of_light_575453_1280x720.jpg" }
    ];
      
  }