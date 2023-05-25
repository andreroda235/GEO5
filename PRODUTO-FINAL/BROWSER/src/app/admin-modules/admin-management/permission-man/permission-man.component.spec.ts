import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PermissionManComponent } from './permission-man.component';

describe('PermissionManComponent', () => {
  let component: PermissionManComponent;
  let fixture: ComponentFixture<PermissionManComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PermissionManComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PermissionManComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
