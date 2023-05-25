import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { FormControl, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';
import {animate, state, style, transition, trigger} from '@angular/animations';


export interface RouteCommentElement {
  routeCommId: string;
  usernameR: string;
  routeName: string;
  routeComment: string;
}


@Component({
  selector: 'app-route-comm',
  templateUrl: './route-comm.component.html',
  styleUrls: ['./route-comm.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class RouteCommComponent implements OnInit {
  expandedElement:RouteCommentElement|null;
  ELEMENT_DATA: RouteCommentElement[] =[];
  dataSource;
  displayedColumns: string[] = ['routeCommId', 'usernameR', 'routeName'];

   //Validators:
   commentId = new FormControl('', [Validators.required]);
   badUsername = new FormControl('',[Validators.required]);
   commentToDel;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(private router : Router, private req: RequestService) { }

  ngOnInit(): void {
    this.req.getAllActiveCommentRoute().subscribe( 
      data=>{console.log(data);
        var arr:RouteCommentElement[]=[];
        data.map(
          e=>{
            arr.push( { routeCommId: e.commentID , usernameR: e.username , routeName: e.routeID , routeComment: e.content } );
          }
        );
        if(arr.length==0){
          alert("Não existem dados para mostrar! There is no data to show!");
        }
        this.ELEMENT_DATA = arr;
        this.dataSource = new MatTableDataSource<RouteCommentElement>(this.ELEMENT_DATA);
        this.dataSource.paginator = this.paginator;
      },(err:HttpErrorResponse)=>{console.log(err)}
    );

  }

  onSubmit(){

    this.req.deactivateRouteComment(this.commentToDel).subscribe(
      data=>{ 
        alert("O utilizador foi sancionado e o comentário desactivado! The user has been sanctioned and the comment has been disabled!");
        this.cancel();
      },(err:HttpErrorResponse)=>{ console.log(err) }
    );

     
  }

  cancel(){
    setTimeout( () => this.router.navigate(['/admin']) , 100 );
  }

}
