import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
//import { HttpErrorResponse } from '@angular/common/http';

import { RequestService } from '../services/RequestService';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css']
})
export class TopBarComponent implements OnInit {
  isLogOutError: boolean = false; 
 
  constructor(private request:RequestService,private router : Router) {   }
  
  ngOnInit(): void {
  }

  logOut(){
    /*
    this.request.logOutUser(localStorage.getItem('tokenID')).subscribe((data : any)=>{
      localStorage.removeItem('tokenID');  
      this.router.navigate(['/home']);
    },
    (err : HttpErrorResponse)=>{
      this.isLogOutError = true;
    });
*/
    localStorage.removeItem('tokenID');
    localStorage.removeItem('username');
    localStorage.removeItem('userInfo');
    localStorage.removeItem('myDirections'); 
    alert("Saiu da sua conta, esperemos vê-lo em breve!");
    setTimeout( () => this.router.navigate(['/home']) , 200 );     
    
  }

}
