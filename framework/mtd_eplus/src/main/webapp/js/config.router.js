'use strict';

/**
 * Config for the router
 */
angular.module('app')
    .run(
        ['$rootScope', '$state', '$stateParams', '$location',
            function ($rootScope, $state, $stateParams, $location) {
                $rootScope.$state = $state;
                $rootScope.$stateParams = $stateParams;
                //Go back to the previous stage with this back() call
                var lastHref = "/home",
                    lastStateName = "home",
                    lastParams = {};
                var historyStates = [];
                var historyParams = [];
                $rootScope.$on("$stateChangeStart", function (event, to, toParams, from, fromParams) {
                    $state.scopePrevious = from;
                    historyStates.push(to);
                    historyParams.push(toParams);
                });
                $rootScope.$on("$stateChangeSuccess", function (event, toState, toParams
                    , fromState, fromParams) {
                    lastStateName = fromState.name;
                    lastParams = fromParams;
                    lastHref = $state.href(lastStateName, lastParams);
                    $rootScope.title = toState.title || "Expedite Plus";
                });
                $rootScope.back = function () {
                    historyStates.pop();
                    historyParams.pop();
                    //var sn = historyStates[historyStates.length - 1];
                    //var sp = historyParams[historyParams.length - 1];
                    var sn = historyStates.splice(-1)[0];
                    var sp = historyParams.splice(-1)[0];

                    $state.go(sn, sp);

                };

            }
        ]
    )
    .config(
        ['$stateProvider', '$urlRouterProvider', 'JQ_CONFIG', 'MODULE_CONFIG', '$httpProvider',
            function ($stateProvider, $urlRouterProvider, JQ_CONFIG, MODULE_CONFIG, $httpProvider) {
                if (!$httpProvider.defaults.headers.get) {
                    $httpProvider.defaults.headers.get = {};
                }

                // Answer edited to include suggestions from comments
                // because previous version of code introduced browser-related errors

                //disable IE ajax request caching
                $httpProvider.defaults.headers.get['If-Modified-Since'] = 'Mon, 26 Jul 1997 05:00:00 GMT';
                // extra
                $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
                $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
                var layout = "tpl/app.html";
                if (window.location.href.indexOf("material") > 0) {
                    layout = "tpl/blocks/material.layout.html";
                    $urlRouterProvider
                        .otherwise('/app/dashboard-v3');
                } else if (window.location.href.indexOf("login") > 0) {
                    $urlRouterProvider
                        .otherwise('/app/operation_console/operation_center');
                } else {
                    $urlRouterProvider
                        .otherwise('/app/dashboard-v1');
                }

                $stateProvider
                    .state('app', {
                        abstract: true,
                        url: '/app',
                        templateUrl: layout
                    })
                    .state('app.dashboard-v1', {
                        title: "Dashboard",
                        url: '/dashboard-v1',
                        templateUrl: 'tpl/app_dashboard_v1.html',
                        resolve: load(['js/controllers/Dashboard/departSummary.js'])
                    })
                    .state('app.dashboard-v2', {
                        url: '/dashboard-v2',
                        templateUrl: 'tpl/app_dashboard_v2.html',
                        resolve: load(['js/controllers/Dashboard/departSummary.js'])
                    })
                    .state('app.dashboard-v3', {
                        url: '/dashboard-v3',
                        templateUrl: 'tpl/app_dashboard_v3.html',
                        resolve: load(['js/controllers/Dashboard/departSummary.js'])
                    })
                    .state('app.ui', {
                        url: '/ui',
                        template: '<div ui-view class="fade-in-up"></div>'
                    })
                    .state('app.ui.buttons', {
                        url: '/buttons',
                        templateUrl: 'tpl/ui_buttons.html'
                    })
                    .state('app.ui.icons', {
                        url: '/icons',
                        templateUrl: 'tpl/ui_icons.html'
                    })
                    .state('app.ui.grid', {
                        url: '/grid',
                        templateUrl: 'tpl/ui_grid.html'
                    })
                    .state('app.ui.widgets', {
                        url: '/widgets',
                        templateUrl: 'tpl/ui_widgets.html'
                    })
                    .state('app.ui.bootstrap', {
                        url: '/bootstrap',
                        templateUrl: 'tpl/ui_bootstrap.html'
                    })
                    .state('app.ui.sortable', {
                        url: '/sortable',
                        templateUrl: 'tpl/ui_sortable.html'
                    })
                    .state('app.ui.scroll', {
                        url: '/scroll',
                        templateUrl: 'tpl/ui_scroll.html',
                        resolve: load('js/controllers/scroll.js')
                    })
                    .state('app.ui.portlet', {
                        url: '/portlet',
                        templateUrl: 'tpl/ui_portlet.html'
                    })
                    .state('app.ui.timeline', {
                        url: '/timeline',
                        templateUrl: 'tpl/ui_timeline.html'
                    })
                    .state('app.ui.tree', {
                        url: '/tree',
                        templateUrl: 'tpl/ui_tree.html',
                        resolve: load(['angularBootstrapNavTree', 'js/controllers/tree.js'])
                    })
                    .state('app.ui.toaster', {
                        url: '/toaster',
                        templateUrl: 'tpl/ui_toaster.html',
                        resolve: load(['toaster', 'js/controllers/toaster.js'])
                    })
                    .state('app.ui.jvectormap', {
                        url: '/jvectormap',
                        templateUrl: 'tpl/ui_jvectormap.html',
                        resolve: load('js/controllers/vectormap.js')
                    })
                    .state('app.ui.googlemap', {
                        url: '/googlemap',
                        templateUrl: 'tpl/ui_googlemap.html',
                        resolve: load(['js/app/map/load-google-maps.js', 'js/app/map/ui-map.js', 'js/app/map/map.js'], function () {
                            return loadGoogleMaps();
                        })
                    })
                    .state('app.chart', {
                        url: '/chart',
                        templateUrl: 'tpl/ui_chart.html',
                        resolve: load('js/controllers/chart.js')
                    })
                    .state('app.system_maintenance', {
                        url: '/system_maintenance',
                        template: '<div ui-view></div>'
                    })
                    .state('app.system_maintenance.epcodes_management', {
                        title: "Epcodes Management",
                        url: '/epcodes_management',
                        templateUrl: 'tpl/system_maintenance/epcodes_management.html',
                        controller: 'epcodesmanagectrl',
                        resolve: load(['xeditable', 'toaster', 'js/controllers/system_maintenance/epcodes_management.js'])
                    })
                    .state('app.system_maintenance.department_list', {
                        title: "Department List",
                        url: '/department_list',
                        templateUrl: 'tpl/system_maintenance/department_list.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/system_maintenance/department_list.js'])
                    })
                    .state('app.system_maintenance.department_edit', {
                        title: "Department Edit",
                        url: '/department_edit',
                        params: {
                            departmentId: null
                        },
                        templateUrl: 'tpl/system_maintenance/department_edit.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/system_maintenance/department_edit.js'])
                    })
                    .state('app.system_maintenance.role_list', {
                        title: "Role List",
                        url: '/role_list',
                        templateUrl: 'tpl/system_maintenance/role_list.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/system_maintenance/role_list.js'])
                    })
                    .state('app.system_maintenance.role_edit', {
                        title: "Role Edit",
                        url: '/role_edit',
                        params: {
                            roleId: null,
                            status: null
                        },
                        templateUrl: 'tpl/system_maintenance/role_edit.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/system_maintenance/role_edit.js'])
                    })
                    .state('app.system_maintenance.quote_template_list', {
                        title: "Quote Template List",
                        url: '/quote_template_list',
                        templateUrl: 'tpl/system_maintenance/quote_template_list.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/system_maintenance/quote_template_list.js'])
                    })
                    .state('app.system_maintenance.quote_templates', {
                        title: "Quote Templates",
                        url: '/quote_templates',
                        params: {
                            tripType: null,
                            name: null
                        },
                        templateUrl: 'tpl/system_maintenance/quote_template.html',
                        resolve: load(['toaster', 'js/controllers/system_maintenance/quote_templates.js'])
                    })
                    .state('app.system_maintenance.quote_template_edit', {
                        title: "Quote Template Edite",
                        url: '/quote_template_edit',
                        templateUrl: 'tpl/system_maintenance/quote_template_edit.html',
                        resolve: load(['toaster', 'js/controllers/system_maintenance/quote_template_edit.js'])
                    })
                    .state('app.system_maintenance.event_template_list', {
                        title: "Event Template List",
                        url: '/event_template_list',
                        templateUrl: 'tpl/system_maintenance/event_template_list.html',
                        resolve: load(['toaster', 'smart-table', 'js/controllers/system_maintenance/event_template_list.js'])
                    })
                    .state('app.system_maintenance.event_templates', {
                        title: "Event Templates",
                        url: '/event_templates',
                        params: {
                            tripType: null,
                            name: null
                        },
                        templateUrl: 'tpl/system_maintenance/event_template.html',
                        resolve: load(['toaster', 'ui.tree', 'ui.select', 'js/controllers/system_maintenance/event_templates.js'])
                    })
                    .state('app.system_maintenance.terms_conditions_templates', {
                        title: "Terms Conditions Templates",
                        url: '/terms_conditions_templates',
                        templateUrl: 'tpl/system_maintenance/terms_conditions_templates.html',
                        resolve: load(['toaster', 'ui.tree', 'js/controllers/system_maintenance/terms_conditions_templates.js'])
                    })
                    .state('app.system_maintenance.terms_conditions_template_edit', {
                        title: "Terms Conditions Template Edit",
                        url: '/terms_conditions_template_edit',
                        templateUrl: 'tpl/system_maintenance/terms_conditions_template_edit.html',
                        resolve: load(['toaster', 'js/controllers/system_maintenance/terms_conditions_template_edit.js'])
                    })
                    .state('app.system_maintenance.user_list', {
                        title: "User List",
                        url: '/user_list',
                        templateUrl: 'tpl/system_maintenance/user_list.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/system_maintenance/user_list.js'])
                    })
                    .state('app.system_maintenance.user_edit', {
                        title: "User Edit",
                        url: '/user_edit',
                        params: {
                            euId: null,
                            status: null
                        },
                        templateUrl: 'tpl/system_maintenance/user_edit.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/system_maintenance/user_edit.js'])
                    })
                    .state('app.operation_console', {
                        url: '/operation_console',
                        template: '<div ui-view></div>'
                    })
                    .state('app.invoice_management', {

                        url: '/invoice_management',
                        template: '<div ui-view></div>'
                    })
                    .state('app.invoice_management.invoices', {
                        title: "Invoice Management",
                        url: '/invoice_management',
                        templateUrl: 'tpl/invoice_management/invoices.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/invoice_management/invoices.js'])
                    })
                    .state('app.operation_console.operation_center', {
                        title: "Operation Center",
                        url: '/operation_center',
                        templateUrl: 'tpl/operation_console/operation_center.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/operation_console/operation_center.js'])
                    })
                    .state('app.operation_console.comm_center', {
                        title: "Communication Center",
                        url: '/comm_center',
                        templateUrl: 'tpl/operation_console/comm_center.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/operation_console/comm_center.js'])
                    })
                    .state('app.operation_console.quote_list', {
                        title: "Quote List",
                        url: '/quote_list',
                        templateUrl: 'tpl/operation_console/quote_list.html',
                        params: {
                            departmentId: 0,
                            tripStatus: 'OPEN',
                            tripType: 'quote',
                            pointInTime: "",
                            startDate: "",
                            endDate: "",
                            searchKey: "",
                            chargeCode: "",
                            triggerMenu: true
                        },
                        resolve: load(['smart-table', 'toaster', 'ui.select', 'js/controllers/operation_console/quote_list.js'])
                    })
                    .state('app.operation_console.trip_list', {
                        title: "Trip List",
                        url: '/trip_list',
                        templateUrl: 'tpl/operation_console/quote_list.html',
                        params: {
                            departmentId: 0,
                            tripStatus: 'OPEN',
                            tripType: 'trip',
                            pointInTime: "",
                            startDate: "",
                            endDate: "",
                            searchKey: "",
                            searchAWB: "",
                            chargeCode: "",
                            triggerMenu: true
                        },
                        resolve: load(['smart-table', 'toaster', 'ui.select', 'js/controllers/operation_console/quote_list.js'])
                    })
                    .state('app.operation_console.quote_details', {
                        title: "Quote Details",
                        url: '/quote_details',
                        templateUrl: 'tpl/operation_console/quote_details.html',
                        params: {
                            tripid: null,
                            tripmode: null,
                            triptype: null,
                            dept: null
                        },
                        resolve: load(['smart-table', 'toaster', 'ui.tree', 'ui.select', 'js/controllers/operation_console/quote_details.js'])
                    })
                    .state('app.operation_console.quote_builder', {
                        title: "Quote Builder",
                        url: '/quote_builder',
                        templateUrl: 'tpl/operation_console/quote_builder.html',
                        params: {
                            quoteid: null
                        },
                        resolve: load(['smart-table', 'toaster', 'js/controllers/operation_console/quote_builder.js'])
                    })
                    .state('app.operation_console.trip_details', {
                        title: "Trip Details",
                        url: '/trip_details',
                        templateUrl: 'tpl/operation_console/trip_details.html',
                        params: {
                            tripid: null,
                            tripmode: null,
                            triptype: null,
                            dept: null
                        },
                        resolve: load(['smart-table', 'toaster', 'darthwade.loading', 'ui.tree', 'ui.select', 'js/controllers/operation_console/trip_details.js'])
                    })
                    .state('app.operation_console.pickup_details', {
                        title: "Pickup Details",
                        url: '/pickup_details',
                        templateUrl: 'tpl/operation_console/pickup_details.html',
                        params: {
                            tripid: null,
                            tripmode: null,
                            triptype: null
                        },
                        resolve: load(['smart-table', 'toaster', 'ui.tree', 'ui.select', 'js/controllers/operation_console/pickup_details.js'])
                    })
                    .state('app.operation_console.bol_details', {
                        title: "Bol Details",
                        url: '/bol_details',
                        templateUrl: 'tpl/operation_console/bol_details.html',
                        params: {
                            tripid: null,
                            tripmode: null,
                            triptype: null
                        },
                        resolve: load(['smart-table', 'toaster', 'ui.tree', 'ui.select', 'js/controllers/operation_console/bol_details.js'])
                    })
                    .state('app.operation_console.invoice_details', {
                        title: "Invoice Details",
                        url: '/invoice_details',
                        templateUrl: 'tpl/operation_console/invoice_details.html',
                        params: {
                            tripid: null,
                            invoiceid: null,
                            tripmode: null,
                            triptype: null

                        },
                        resolve: load(['smart-table', 'toaster', 'ui.tree', 'ui.select', 'js/controllers/operation_console/invoice_details.js'])
                    })
                    .state('app.operation_console.courier_list', {
                        title: "Courier List",
                        url: '/courier_list',
                        templateUrl: 'tpl/operation_console/courier_list.html',
                        params: {
                            companyId: 0,
                            locationId: 0,
                            status: null,
                            country: ","
                        },
                        resolve: load(['smart-table', 'toaster', 'ui.tree', 'ui.select', 'js/controllers/operation_console/courier_list.js'])
                    })
                    .state('app.system_maintenance.entity_list', {
                        title: "Entity List",
                        url: '/entity_list',
                        templateUrl: 'tpl/system_maintenance/entity_list.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/system_maintenance/entity_list.js'])
                    })
                    .state('app.system_maintenance.entity_edit', {
                        title: "Entity Edit",
                        url: '/entity_edit?entityId&selectId&status',
                        params: {
                            entityId: null,
                            selectId: null,
                            status: null
                        },
                        templateUrl: 'tpl/system_maintenance/entity_edit.html',
                        resolve: load(['toaster', 'ui.select', 'js/controllers/system_maintenance/entity_edit.js'])
                    })
                    .state('app.operation_console.locked_trips', {
                        title: "Locked Trips",
                        url: '/locked_trips',
                        templateUrl: 'tpl/operation_console/locked_trips.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/operation_console/locked_trips.js'])
                    })
                    .state('app.reports', {
                        url: '/reports',
                        template: '<div ui-view></div>'
                    })
                    .state('app.reports.tsaairport', {
                        title: "Tsa Ariport",
                        url: '/tsaairport',
                        templateUrl: 'tpl/reports/TSAAirport.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/reports/TSAAirport.js'])
                    })
                    .state('app.reports.trip', {
                        title: "Trip",
                        url: '/trip',
                        templateUrl: 'tpl/reports/Trip.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/reports/Trip.js'])
                    })
                    .state('app.reports.currency', {
                        title: "Currency",
                        url: '/currency',
                        templateUrl: 'tpl/reports/Currency.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/reports/Currency.js'])
                    })
                    .state('app.reports.sale', {
                        title: "Sale",
                        url: '/sale',
                        templateUrl: 'tpl/reports/Sale.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/reports/Sale.js'])
                    })
                    .state('app.reports.service', {
                        title: "Service",
                        url: '/service',
                        templateUrl: 'tpl/reports/Service.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/reports/Service.js'])
                    })
                    .state('app.reports.cost', {
                        Title: "Cost",
                        url: '/cost',
                        templateUrl: 'tpl/reports/Cost.html',
                        resolve: load(['smart-table', 'toaster', 'js/controllers/reports/Cost.js'])
                    })
                    // table
                    .state('app.table', {
                        url: '/table',
                        template: '<div ui-view></div>'
                    })
                    .state('app.table.static', {
                        url: '/static',
                        templateUrl: 'tpl/table_static.html'
                    })
                    .state('app.table.datatable', {
                        url: '/datatable',
                        templateUrl: 'tpl/table_datatable.html'
                    })
                    .state('app.table.footable', {
                        url: '/footable',
                        templateUrl: 'tpl/table_footable.html'
                    })
                    .state('app.table.grid', {
                        url: '/grid',
                        templateUrl: 'tpl/table_grid.html',
                        resolve: load(['ngGrid', 'js/controllers/grid.js'])
                    })
                    .state('app.table.uigrid', {
                        url: '/uigrid',
                        templateUrl: 'tpl/table_uigrid.html',
                        resolve: load(['ui.grid', 'js/controllers/uigrid.js'])
                    })
                    .state('app.table.editable', {
                        url: '/editable',
                        templateUrl: 'tpl/table_editable.html',
                        controller: 'XeditableCtrl',
                        resolve: load(['xeditable', 'js/controllers/xeditable.js'])
                    })
                    .state('app.table.smart', {
                        url: '/smart',
                        templateUrl: 'tpl/table_smart.html',
                        resolve: load(['smart-table', 'js/controllers/table.js'])
                    })
                    // form
                    .state('app.form', {
                        url: '/form',
                        template: '<div ui-view class="fade-in"></div>',
                        resolve: load('js/controllers/form.js')
                    })
                    .state('app.form.components', {
                        url: '/components',
                        templateUrl: 'tpl/form_components.html',
                        resolve: load(['ngBootstrap', 'daterangepicker', 'js/controllers/form.components.js'])
                    })
                    .state('app.form.elements', {
                        url: '/elements',
                        templateUrl: 'tpl/form_elements.html'
                    })

                    .state('app.form.validation', {
                        url: '/validation',
                        templateUrl: 'tpl/form_validation.html'
                    })
                    .state('app.form.wizard', {
                        url: '/wizard',
                        templateUrl: 'tpl/form_wizard.html'
                    })
                    .state('app.form.fileupload', {
                        url: '/fileupload',
                        templateUrl: 'tpl/form_fileupload.html',
                        resolve: load(['angularFileUpload', 'js/controllers/file-upload.js'])
                    })
                    .state('app.form.imagecrop', {
                        url: '/imagecrop',
                        templateUrl: 'tpl/form_imagecrop.html',
                        resolve: load(['ngImgCrop', 'js/controllers/imgcrop.js'])
                    })
                    .state('app.form.select', {
                        url: '/select',
                        templateUrl: 'tpl/form_select.html',
                        controller: 'SelectCtrl',
                        resolve: load(['ui.select', 'js/controllers/select.js'])
                    })
                    .state('app.form.slider', {
                        url: '/slider',
                        templateUrl: 'tpl/form_slider.html',
                        controller: 'SliderCtrl',
                        resolve: load(['vr.directives.slider', 'js/controllers/slider.js'])
                    })
                    .state('app.form.editor', {
                        url: '/editor',
                        templateUrl: 'tpl/form_editor.html',
                        controller: 'EditorCtrl',
                        resolve: load(['textAngular', 'js/controllers/editor.js'])
                    })
                    .state('app.form.xeditable', {
                        url: '/xeditable',
                        templateUrl: 'tpl/form_xeditable.html',
                        controller: 'XeditableCtrl',
                        resolve: load(['xeditable', 'js/controllers/xeditable.js'])
                    })
                    // pages
                    .state('app.page', {
                        url: '/page',
                        template: '<div ui-view class="fade-in-down"></div>'
                    })
                    .state('app.page.profile', {
                        url: '/profile',
                        templateUrl: 'tpl/page_profile.html'
                    })
                    .state('app.page.post', {
                        url: '/post',
                        templateUrl: 'tpl/page_post.html'
                    })
                    .state('app.page.search', {
                        url: '/search',
                        templateUrl: 'tpl/page_search.html'
                    })
                    .state('app.page.invoice', {
                        url: '/invoice',
                        templateUrl: 'tpl/page_invoice.html'
                    })
                    .state('app.page.price', {
                        url: '/price',
                        templateUrl: 'tpl/page_price.html'
                    })
                    .state('app.docs', {
                        url: '/docs',
                        templateUrl: 'tpl/docs.html'
                    })
                    // others
                    .state('lockme', {
                        url: '/lockme',
                        templateUrl: 'tpl/page_lockme.html'
                    })
                    .state('access', {
                        url: '/access',
                        template: '<div ui-view class="fade-in-right-big smooth"></div>'
                    })
                    .state('access.signin', {
                        title: "Signin",
                        url: '/signin',
                        templateUrl: 'tpl/page_signin.html',
                        resolve: load(['js/controllers/signin.js'])
                    })
                    .state('access.signup', {
                        url: '/signup',
                        templateUrl: 'tpl/page_signup.html',
                        resolve: load(['js/controllers/signup.js'])
                    })
                    .state('access.forgotpwd', {
                        url: '/forgotpwd',
                        templateUrl: 'tpl/page_forgotpwd.html'
                    })
                    .state('access.404', {
                        url: '/404',
                        templateUrl: 'tpl/page_404.html'
                    })

                    // fullCalendar
                    .state('app.calendar', {
                        url: '/calendar',
                        templateUrl: 'tpl/app_calendar.html',
                        // use resolve to load other dependences
                        resolve: load(['moment', 'fullcalendar', 'ui.calendar', 'js/app/calendar/calendar.js'])
                    })

                    // mail
                    .state('app.mail', {
                        abstract: true,
                        url: '/mail',
                        templateUrl: 'tpl/mail.html',
                        // use resolve to load other dependences
                        resolve: load(['js/app/mail/mail.js', 'js/app/mail/mail-service.js', 'moment'])
                    })
                    .state('app.mail.list', {
                        url: '/inbox/{fold}',
                        templateUrl: 'tpl/mail.list.html'
                    })
                    .state('app.mail.detail', {
                        url: '/{mailId:[0-9]{1,4}}',
                        templateUrl: 'tpl/mail.detail.html'
                    })
                    .state('app.mail.compose', {
                        url: '/compose',
                        templateUrl: 'tpl/mail.new.html'
                    })

                    .state('layout', {
                        abstract: true,
                        url: '/layout',
                        templateUrl: 'tpl/layout.html'
                    })
                    .state('layout.fullwidth', {
                        url: '/fullwidth',
                        views: {
                            '': {
                                templateUrl: 'tpl/layout_fullwidth.html'
                            },
                            'footer': {
                                templateUrl: 'tpl/layout_footer_fullwidth.html'
                            }
                        },
                        resolve: load(['js/controllers/vectormap.js'])
                    })
                    .state('layout.mobile', {
                        url: '/mobile',
                        views: {
                            '': {
                                templateUrl: 'tpl/layout_mobile.html'
                            },
                            'footer': {
                                templateUrl: 'tpl/layout_footer_mobile.html'
                            }
                        }
                    })
                    .state('layout.app', {
                        url: '/app',
                        views: {
                            '': {
                                templateUrl: 'tpl/layout_app.html'
                            },
                            'footer': {
                                templateUrl: 'tpl/layout_footer_fullwidth.html'
                            }
                        },
                        resolve: load(['js/controllers/tab.js'])
                    })
                    .state('apps', {
                        abstract: true,
                        url: '/apps',
                        templateUrl: 'tpl/layout.html'
                    })
                    .state('apps.note', {
                        url: '/note',
                        templateUrl: 'tpl/apps_note.html',
                        resolve: load(['js/app/note/note.js', 'moment'])
                    })
                    .state('apps.contact', {
                        url: '/contact',
                        templateUrl: 'tpl/apps_contact.html',
                        resolve: load(['js/app/contact/contact.js'])
                    })
                    .state('app.weather', {
                        url: '/weather',
                        templateUrl: 'tpl/apps_weather.html',
                        resolve: load(['js/app/weather/skycons.js', 'angular-skycons', 'js/app/weather/ctrl.js', 'moment'])
                    })
                    .state('app.todo', {
                        url: '/todo',
                        templateUrl: 'tpl/apps_todo.html',
                        resolve: load(['js/app/todo/todo.js', 'moment'])
                    })
                    .state('app.todo.list', {
                        url: '/{fold}'
                    })
                    .state('app.note', {
                        url: '/note',
                        templateUrl: 'tpl/apps_note_material.html',
                        resolve: load(['js/app/note/note.js', 'moment'])
                    })
                    .state('music', {
                        url: '/music',
                        templateUrl: 'tpl/music.html',
                        controller: 'MusicCtrl',
                        resolve: load([
                            'com.2fdevs.videogular',
                            'com.2fdevs.videogular.plugins.controls',
                            'com.2fdevs.videogular.plugins.overlayplay',
                            'com.2fdevs.videogular.plugins.poster',
                            'com.2fdevs.videogular.plugins.buffering',
                            'js/app/music/ctrl.js',
                            'js/app/music/theme.css'
                        ])
                    })
                    .state('music.home', {
                        url: '/home',
                        templateUrl: 'tpl/music.home.html'
                    })
                    .state('music.genres', {
                        url: '/genres',
                        templateUrl: 'tpl/music.genres.html'
                    })
                    .state('music.detail', {
                        url: '/detail',
                        templateUrl: 'tpl/music.detail.html'
                    })
                    .state('music.mtv', {
                        url: '/mtv',
                        templateUrl: 'tpl/music.mtv.html'
                    })
                    .state('music.mtvdetail', {
                        url: '/mtvdetail',
                        templateUrl: 'tpl/music.mtv.detail.html'
                    })
                    .state('music.playlist', {
                        url: '/playlist/{fold}',
                        templateUrl: 'tpl/music.playlist.html'
                    })
                    .state('app.material', {
                        url: '/material',
                        template: '<div ui-view class="wrapper-md"></div>',
                        resolve: load(['js/controllers/material.js'])
                    })
                    .state('app.material.button', {
                        url: '/button',
                        templateUrl: 'tpl/material/button.html'
                    })
                    .state('app.material.color', {
                        url: '/color',
                        templateUrl: 'tpl/material/color.html'
                    })
                    .state('app.material.icon', {
                        url: '/icon',
                        templateUrl: 'tpl/material/icon.html'
                    })
                    .state('app.material.card', {
                        url: '/card',
                        templateUrl: 'tpl/material/card.html'
                    })
                    .state('app.material.form', {
                        url: '/form',
                        templateUrl: 'tpl/material/form.html'
                    })
                    .state('app.material.list', {
                        url: '/list',
                        templateUrl: 'tpl/material/list.html'
                    })
                    .state('app.material.ngmaterial', {
                        url: '/ngmaterial',
                        templateUrl: 'tpl/material/ngmaterial.html'
                    });

                function load(srcs, callback) {
                    return {
                        deps: ['$ocLazyLoad', '$q',
                            function ($ocLazyLoad, $q) {
                                var deferred = $q.defer();
                                var promise = false;
                                srcs = angular.isArray(srcs) ? srcs : srcs.split(/\s+/);
                                if (!promise) {
                                    promise = deferred.promise;
                                }
                                angular.forEach(srcs, function (src) {
                                    promise = promise.then(function () {
                                        if (JQ_CONFIG[src]) {
                                            return $ocLazyLoad.load(JQ_CONFIG[src]);
                                        }
                                        angular.forEach(MODULE_CONFIG, function (module) {
                                            if (module.name == src) {
                                                name = module.name;
                                            } else {
                                                name = src;
                                            }
                                        });
                                        return $ocLazyLoad.load(name);
                                    });
                                });
                                deferred.resolve();
                                return callback ? promise.then(function () {
                                    return callback();
                                }) : promise;
                            }]
                    }
                }


            }
        ]
    );
