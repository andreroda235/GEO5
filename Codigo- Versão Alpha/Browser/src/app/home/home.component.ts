import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as $ from 'jquery';
import { RequestService } from '../services/RequestService';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  l: string;
  isImageLoading: boolean;
  imageToShow: string | ArrayBuffer;
  constructor( public translate: TranslateService ,private req: RequestService ) {
    translate.addLangs(['pt','en']);
   }

   public sol 
   public cam 
   public bg
   public serra
  ngOnInit(): void {

    

    this.l = localStorage.getItem('language');
    if(this.l===null) this.l='pt';

    this.translate.setDefaultLang(this.l);

    $(document).ready(function() {
      $('select').val( localStorage.getItem('language') );
      
    });

  }

  switchLang(lang: string) {
    localStorage.setItem('language',lang);
    this.translate.use(lang);
    this.translate.setDefaultLang(lang);
  }
}
