import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FlexLayoutModule } from '@angular/flex-layout';
import {FullscreenOverlayContainer, OverlayContainer} from '@angular/cdk/overlay';

//import { NbThemeModule } from '@nebular/theme';
import { NbThemeModule, NbSidebarModule, NbLayoutModule, NbCardModule } from '@nebular/theme';

import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { MatSidenavModule } from '@angular/material/sidenav';
import { MatDividerModule } from '@angular/material/divider';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

import { AdminDefaultComponent } from './admin-default.component';
import { DashboardComponent } from 'src/app/admin-modules/dashboard/dashboard.component';
import { InfosComponent } from 'src/app/admin-modules/infos/infos.component';
import { AdminModule } from 'src/app/admin/admin.module';
import { DashboardService } from 'src/app/services/dashboard.service';
import { AdminManagementComponent } from 'src/app/admin-modules/admin-management/admin-management.component';
import { UserManagementComponent } from 'src/app/admin-modules/user-management/user-management.component';
import { MapManagementComponent } from 'src/app/admin-modules/map-management/map-management.component';
import { QuizzesComponent } from '../../admin-modules/map-management/quizzes/quizzes.component';
import { PermissionManComponent } from '../../admin-modules/admin-management/permission-man/permission-man.component'

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AgmCoreModule } from '@agm/core';
import { HttpClient } from '@angular/common/http';
import { RateUserComponent } from 'src/app/admin-modules/user-management/rate-user/rate-user.component';
import { DeleteUserComponent } from 'src/app/admin-modules/user-management/delete-user/delete-user.component';
import { GeostopCommComponent } from 'src/app/admin-modules/infos/geostop-comm/geostop-comm.component';
import { RouteCommComponent } from 'src/app/admin-modules/infos/route-comm/route-comm.component';

@NgModule({
  declarations: [
    AdminDefaultComponent,
    DashboardComponent,
    InfosComponent,
    AdminManagementComponent,
    UserManagementComponent,
    MapManagementComponent,
    QuizzesComponent,
    PermissionManComponent,
    RateUserComponent,
    DeleteUserComponent,
    GeostopCommComponent,
    RouteCommComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    BrowserAnimationsModule,
    AdminModule,
    MatSidenavModule,
    MatDividerModule,
    MatCardModule,
    MatPaginatorModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatTabsModule,
    MatSelectModule,
    MatButtonModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    FormsModule,
    ReactiveFormsModule,    
    FlexLayoutModule,
    NgSelectModule,
    NbLayoutModule,
    NbSidebarModule,
    NbCardModule,
    NbThemeModule.forRoot({ name: 'default' }),
    AgmCoreModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: httpTranslateLoader,
        deps: [HttpClient]
      }
    })
  ],
  providers: [
    DashboardService,
    {provide: OverlayContainer, useClass: FullscreenOverlayContainer}
  ]
})
export class AdminDefaultModule { }

// AOT compilation support
export function httpTranslateLoader(http: HttpClient) {
  return new TranslateHttpLoader(http);
}
  
