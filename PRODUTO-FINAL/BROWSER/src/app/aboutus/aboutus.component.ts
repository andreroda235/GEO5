import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core'

@Component({
  selector: 'app-aboutus',
  templateUrl: './aboutus.component.html',
  styleUrls: ['./aboutus.component.scss']
})

export class AboutusComponent implements OnInit {

  l:string;

  constructor(public translate: TranslateService) { 

    this.l = localStorage.getItem('language');
    if(this.l==null){
      this.l='pt';
    } 
    this.translate.setDefaultLang(this.l);
  }
  ngOnInit() { }

}