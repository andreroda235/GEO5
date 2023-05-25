import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { FormControl, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';
import {animate, state, style, transition, trigger} from '@angular/animations';
export interface GeoCommentElement {
  geoCommId: string;
  usernameGC: string;
  geostopName: string;
  geoComment: string;
}


@Component({
  selector: 'app-geostop-comm',
  templateUrl: './geostop-comm.component.html',
  styleUrls: ['./geostop-comm.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class GeostopCommComponent implements OnInit {
  expandedElement:GeoCommentElement|null;
  ELEMENT_DATA: GeoCommentElement[] =[];
  dataSource;
  displayedColumns: string[] = ['geoCommId', 'usernameGC', 'geostopName'];

  commentToDel;

   //Validators:
   commentId = new FormControl('', [Validators.required]);y
   badUsername = new FormControl('',[Validators.required]);

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(private router : Router, private req: RequestService ) { }

  ngOnInit(): void {

    this.req.getAllActiveCommentGeo().subscribe(
      data=>{
        var arr:GeoCommentElement[]=[];
        data.map(
          e=>{
            arr.push({ geoCommId: e.commentID , usernameGC:e.username, geostopName:e.geoSpotName, geoComment: e.content });
          }
        );
        if(arr==[]){
          alert("Não existem dados para mostrar! There is no data to show!");
        }
        this.ELEMENT_DATA = arr;
        this.dataSource = new MatTableDataSource<GeoCommentElement>(this.ELEMENT_DATA);
        this.dataSource.paginator = this.paginator;
      },(err:HttpErrorResponse)=>{console.log(err)}

    );
    
  }

  onSubmit(){

    this.req.deactivateGeoComment(this.commentToDel).subscribe(
      data=>{ 
        alert("O utilizador foi sancionado e o comentário desactivado! The user has been sanctioned and the comment has been disabled!");
        this.cancel();
       },(err:HttpErrorResponse)=>{console.log(err)}
    );

     
  }

  cancel(){
    setTimeout( () => this.router.navigate(['/admin']) , 100 );
  }


}
