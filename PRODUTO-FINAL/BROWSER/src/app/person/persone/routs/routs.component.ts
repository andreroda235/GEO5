import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import { RequestService } from 'src/app/services/RequestService';
import { ThrowStmt } from '@angular/compiler';
import { HttpErrorResponse } from '@angular/common/http';

export interface RouteData {
  photo: string;
  title: string;
  description: string;
}



@Component({
  selector: 'app-routs',
  templateUrl: './routs.component.html',
  styleUrls: ['./routs.component.css']
})
export class RoutsComponent implements OnInit {
  dataSource;
  displayedColumns: string[] = ['photo', 'title', 'description'];
  ELEMENT_DATA: RouteData[] = [];

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(private req: RequestService) {
    //this.dataSource = new MatTableDataSource<RouteData>(ELEMENT_DATA);
   }

  ngOnInit() {
    this.ELEMENT_DATA = [];
    this.req.getmyCams().subscribe(
      (data : any )=>{ 
        data.map( element => {
          
          var img = "";
          this.req.getRoutePhotos(element.id).subscribe(
            images=>{
              img = "https://storage.cloud.google.com/apdc-geoproj.appspot.com/"+images[0];
              
              var route: RouteData= {
                photo: img, 
                title: element.title,
                description: element.description
              }
              this.ELEMENT_DATA.push(route);
              this.dataSource= new MatTableDataSource<RouteData>(this.ELEMENT_DATA);  
              this.dataSource.paginator = this.paginator;
              this.dataSource.sort = this.sort;
            }
          );
        });
      },(err: HttpErrorResponse)=>{ console.log(err)}
    );
    
    
  }








  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
