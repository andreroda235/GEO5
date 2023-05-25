import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  @Output() toggleSideBarForMenu: EventEmitter<any> = new EventEmitter()

  constructor(private router : Router) { }

  ngOnInit(): void {}

  toggleSideBar($event: any) {
    this.toggleSideBarForMenu.emit();
    // For resize if needed
    setTimeout(() => {
      window.dispatchEvent(
        new Event('resize')
      );
    }, 300)
  }

  logOut(){
    localStorage.removeItem('tokenID');
    localStorage.removeItem('username');
    localStorage.removeItem('userInfo');
    localStorage.removeItem('myDirections'); 
    alert("Saiu da sua conta, esperemos vê-lo em breve!");
    setTimeout( () => this.router.navigate(['/home']) , 200 );     
  }

}
