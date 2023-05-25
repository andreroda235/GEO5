import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import {animate, state, style, transition, trigger} from '@angular/animations';
import { DashboardService } from 'src/app/services/dashboard.service';
import { RequestService } from 'src/app/services/RequestService';


export interface UserElement {
  nameQ: string;
  descriptionQ: string;
  keywordsQ: string;
  numberQ: number;
  questionsQ:any[]
}


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class DashboardComponent implements OnInit {
  ELEMENT_DATA: UserElement[] =[];  
  expandedElement:UserElement|null;
  
  bigChart = [];
  statsCard = [];
  pieChart = [];

  dataSource ;
  displayedColumns: string[] = ['nameQ','descriptionQ', 'keywordsQ', 'numberQ'];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(private dashboardService: DashboardService , private req: RequestService ) { }

  ngOnInit(): void {
    this.bigChart = this.dashboardService.bigChart();
    this.statsCard = this.dashboardService.statsCard();
    this.pieChart = this.dashboardService.pieChart();

    this.req.getAllQuizzes().subscribe(
      data=>{
        var arr:UserElement[] = []; 
        data.map(
          e=>{
            arr.push( {nameQ: e.title, descriptionQ: e.description, keywordsQ: e.keywords, numberQ: e.questions.length,questionsQ:e.questions } );
          }
        );
        if(arr==[]){
          alert("Não existem dados para mostrar na tabela! There is no data to show in the table!");
        }
        this.ELEMENT_DATA = arr;
        this.dataSource = new MatTableDataSource<UserElement>(this.ELEMENT_DATA);
        this.dataSource.paginator = this.paginator;
      }
    );
  }

}
