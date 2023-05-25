import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { HttpErrorResponse } from '@angular/common/http';
import { RequestService } from 'src/app/services/RequestService';

export interface DelUserElement {
  deleteUsername: string;
  deleteName: string,
  deleteEmail: string,
  deleteRole:string
}


@Component({
  selector: 'app-delete-user',
  templateUrl: './delete-user.component.html',
  styleUrls: ['./delete-user.component.scss']
})
export class DeleteUserComponent implements OnInit {
  ELEMENT_DATA: DelUserElement[] =[];
  dataSource;
  displayedColumns: string[] = ['deleteUsername', 'deleteName', 'deleteEmail', 'deleteRole'];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(private router : Router,private req: RequestService) { }

  ngOnInit(): void {
    this.req.getAllInactiveUsers().subscribe(
      data=>{
        var arr:DelUserElement[]=[];
        data.map(
          e=>{
            arr.push( {deleteUsername: e.key.path[0].name, deleteName: e.properties.user_name.value, deleteEmail: e.properties.user_email.value, deleteRole: e.properties.user_role.value} );
          }
        );
        if(arr.length == 0){
          alert("Não existem dados para mostrar! There is no data to show!");
        }
        this.ELEMENT_DATA = arr;
        this.dataSource = new MatTableDataSource<DelUserElement>(this.ELEMENT_DATA);
        this.dataSource.paginator = this.paginator;  
      },(err:HttpErrorResponse)=>{console.log(err)}
    );

    
  }

  deleteAllU(){

    this.req.deleteAllUserI().subscribe(
      data=>{
        alert("Utilizadores inactivos foram eliminados!");
        this.cancel();
      },(err:HttpErrorResponse)=>{alert("Ups, erro"); console.log(err); }
    ); 
    
    
  }

  cancel(){
    setTimeout( () => this.router.navigate(['/admin']) , 100 );
  }
}
