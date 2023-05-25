import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-start-page',
  templateUrl: './start-page.component.html',
  styleUrls: ['./start-page.component.scss']
})
export class StartPageComponent implements OnInit {
  @ViewChild('myVideo') videoplayer: any;
  constructor(private router : Router) { }

  ngOnInit(): void {
  }

  site(){
    setTimeout( () => this.router.navigate(['/home']) , 100 );
  }

  app(){
    setTimeout( () => this.router.navigate(['/appAndroid']) , 100 );
  }
}
