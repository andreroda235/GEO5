import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { Validators, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';

export interface UserElement {
  username: string;
  email: string;
  role: string;
}

@Component({
  selector: 'app-permission-man',
  templateUrl: './permission-man.component.html',
  styleUrls: ['./permission-man.component.scss']
})
export class PermissionManComponent implements OnInit {

  ELEMENT_DATA: UserElement[] =[];
  dataSource;
  displayedColumns: string[] = ['username', 'email', 'role'];
  selectedValue: string;

  //Validators:
  email = new FormControl('', [Validators.required]);
  username = new FormControl('',[Validators.required]);

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(private router : Router, private req: RequestService ) { }

  ngOnInit(): void {

    this.req.getAllActiveAdmin().subscribe(
      data=>{
        console.log(data);
        var arr:UserElement[]=[];
        data.map(
          e=>{
            arr.push({ username: e.key.path[0].name , email:e.properties.user_email.value, role:e.properties.user_role.value});
          }
        );
        if(arr.length ==0){
          alert("Não existem dados para mostrar! There is no data to show!");
        }
        this.ELEMENT_DATA = arr;
        this.dataSource = new MatTableDataSource<UserElement>(this.ELEMENT_DATA);
        this.dataSource.paginator = this.paginator;
      },(err:HttpErrorResponse)=>{console.log(err)}

    );

  }

  onSubmit(){
    const body={
       username: this.username.value.toString(),
       newRole: this.selectedValue
     }
     this.req.changeAdminRoleTo(body).subscribe(
        res=>{ alert("A função do Administrador foi atualizada! The Admin role was updated successfully!"); this.onCancel(); },
        (err:HttpErrorResponse)=>{alert("Ups Algo correu mal")}
     );
     
  }

  onCancel(){
    setTimeout( () => this.router.navigate(['/admin']) , 100 );
  }
}
