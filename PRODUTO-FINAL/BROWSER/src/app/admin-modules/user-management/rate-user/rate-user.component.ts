import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { FormControl, Validators } from '@angular/forms';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

export interface RateUserElement {
  rateUsername: string;
  rateName: string;
  rateEmail: string;
  active: string;
  posiPoints: number;
  negPoints: number;
}

@Component({
  selector: 'app-rate-user',
  templateUrl: './rate-user.component.html',
  styleUrls: ['./rate-user.component.scss']
})
export class RateUserComponent implements OnInit {
  ELEMENT_DATA: RateUserElement[] = [];

  dataSource;
  displayedColumns: string[] = ['rateUsername','rateName' , 'rateEmail', 'active','posiPoints','negPoints'];
  
  userTargetI;
  userTargetA;

  //Validators:
  usernameInact = new FormControl('',[Validators.required]);
  usernameAct = new FormControl('',[Validators.required]);

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(private req: RequestService,private router: Router) { }

  ngOnInit(): void {
    this.req.getAllUsers().subscribe(
      data=>{
        var arr: RateUserElement[]=[];
        data.map(
          e=>{

            var val = "Não"
            if(e.properties.active_account.value) val="Sim";

            arr.push( {rateUsername: e.key.path[0].name, rateName: e.properties.user_name.value, rateEmail: e.properties.user_email.value, active:val , posiPoints:0, negPoints:0} );
          }
        );
        if(arr==[]){
          alert("Não existem dados para mostrar! There is no data to show!");
        }
        this.ELEMENT_DATA = arr;
        this.dataSource = new MatTableDataSource<RateUserElement>(this.ELEMENT_DATA);
        this.dataSource.paginator = this.paginator;  
      },(err:HttpErrorResponse)=>{console.log(err)}
    );

    
  }

  onSubmitInact(){
    const body={
      username: this.userTargetI
    }
    console.log(body);
    console.log(this.userTargetI)
    this.req.deactivateAccCommunity(body).subscribe(
      data=>{alert("A conta foi desactivada! The user account is inactive!");
      setTimeout( () => this.router.navigate(['/admin']) , 100 );
    },(err:HttpErrorResponse)=>{ console.log(err)}
    );
    //Falta ligação com o servidor!
    
  }

  onSubmitAct(){
    const body={
      username: this.userTargetA
    }

    this.req.activateAccCommunity(body).subscribe(
      data=>{alert("A conta foi activada! The user account is active!");
      setTimeout( () => this.router.navigate(['/admin']) , 100 );
    },(err:HttpErrorResponse)=>{ console.log(err)}
    );    
  }

}
