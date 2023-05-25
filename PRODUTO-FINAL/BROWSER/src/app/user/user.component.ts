import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { RequestService } from '../services/RequestService';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  isRegError : boolean = false;

  constructor(private request:RequestService,private router:Router) { }

  ngOnInit(): void {
  }


  OnSubmit(Secret){
var usern = localStorage.getItem('username');
    const zz={
     username:usern,
     activationCode:Secret
    }
    this.isRegError = true;

    this.request.userActAcc(zz).subscribe((data : any)=>{


    const mm ={
      username:usern,
      password:localStorage.getItem('password')
    }
     this.request.userAuthentication(mm).subscribe((data : any)=>{
      
      
      localStorage.setItem('tokenID',data);
      localStorage.removeItem('password');
      setTimeout( () => this.router.navigate(['/person']) , 300 );     
     
    },(err : HttpErrorResponse)=>{ 
      this.isRegError = true;
     setTimeout(()=>this.isRegError = false,1000);
     });
    },(err : HttpErrorResponse)=>{ 
      this.isRegError = true;
     setTimeout(()=>this.isRegError = false,1000);
     });
    
    }
}
