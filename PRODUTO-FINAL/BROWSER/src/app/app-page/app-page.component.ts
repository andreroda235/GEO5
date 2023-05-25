import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-app-page',
  templateUrl: './app-page.component.html',
  styleUrls: ['./app-page.component.scss']
})
export class AppPageComponent implements OnInit {

  @ViewChild('myVideoApp') videoplayer: any;

  constructor(private router : Router) { }
  

  ngOnInit(): void {
  
  }

  site(){
    setTimeout( () => this.router.navigate(['/home']) , 100 );
  }

}
