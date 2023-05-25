import { Component, OnInit, ViewChild } from '@angular/core';
import { DashboardService } from 'src/app/services/dashboard.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';

export interface UserElement {
  name: string;
  position: number;
  email: string;
  accountType: string;
}

const ELEMENT_DATA: UserElement[] =[
  {position: 1, name: 'Luis', email: 'luis@dashboard.com', accountType: 'User'},
  {position: 2, name: 'Manuel', email: 'manuel@dashboard.com', accountType: 'User'},
  {position: 3, name: 'Pedro', email: 'pedro@dashboard.com', accountType: 'User'},
  {position: 4, name: 'Maria', email: 'maria@dashboard.com', accountType: 'User'},
  {position: 5, name: 'Sara', email: 'sara@dashboard.com', accountType: 'Routes Admin'},  
  {position: 6, name: 'Alex', email: 'alex@dashboard.com', accountType: 'Routes Admin'},
  {position: 7, name: 'Edson', email: 'edson@dashboard.com', accountType: 'Comunity Moderator'},
  {position: 8, name: 'Andre', email: 'andre@dashboard.com', accountType: 'Comunity Moderator'},
  {position: 9, name: 'Alexandre', email: 'manuel@dashboard.com', accountType: 'Routes Admin'},
  {position: 10, name: 'GEO5Sol', email: 'geo5sol@dashboard', accountType: 'Super Admin'},
];

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  bigChart = [];
  statsCard = [];
  pieChart = [];
  dataSource = new MatTableDataSource<UserElement>(ELEMENT_DATA);
  displayedColumns: string[] = ['position', 'name', 'email', 'accountType'];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(private dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.bigChart = this.dashboardService.bigChart();
    this.statsCard = this.dashboardService.statsCard();
    this.pieChart = this.dashboardService.pieChart();

    this.dataSource.paginator = this.paginator;
  }

}
