﻿'------------------------------------------------------------------------------
' <auto-generated>
'     This code was generated by a tool.
'     Runtime Version:2.0.50727.8009
'
'     Changes to this file may cause incorrect behavior and will be lost if
'     the code is regenerated.
' </auto-generated>
'------------------------------------------------------------------------------

Option Strict On
Option Explicit On

Imports System

Namespace My.Resources
    
    'This class was auto-generated by the StronglyTypedResourceBuilder
    'class via a tool like ResGen or Visual Studio.
    'To add or remove a member, edit your .ResX file then rerun ResGen
    'with the /str option, or rebuild your VS project.
    '''<summary>
    '''  A strongly-typed resource class, for looking up localized strings, etc.
    '''</summary>
    <Global.System.CodeDom.Compiler.GeneratedCodeAttribute("System.Resources.Tools.StronglyTypedResourceBuilder", "2.0.0.0"),  _
     Global.System.Diagnostics.DebuggerNonUserCodeAttribute(),  _
     Global.System.Runtime.CompilerServices.CompilerGeneratedAttribute(),  _
     Global.Microsoft.VisualBasic.HideModuleNameAttribute()>  _
    Friend Module Resources
        
        Private resourceMan As Global.System.Resources.ResourceManager
        
        Private resourceCulture As Global.System.Globalization.CultureInfo
        
        '''<summary>
        '''  Returns the cached ResourceManager instance used by this class.
        '''</summary>
        <Global.System.ComponentModel.EditorBrowsableAttribute(Global.System.ComponentModel.EditorBrowsableState.Advanced)>  _
        Friend ReadOnly Property ResourceManager() As Global.System.Resources.ResourceManager
            Get
                If Object.ReferenceEquals(resourceMan, Nothing) Then
                    Dim temp As Global.System.Resources.ResourceManager = New Global.System.Resources.ResourceManager("Neurotec.Samples.Resources", GetType(Resources).Assembly)
                    resourceMan = temp
                End If
                Return resourceMan
            End Get
        End Property
        
        '''<summary>
        '''  Overrides the current thread's CurrentUICulture property for all
        '''  resource lookups using this strongly typed resource class.
        '''</summary>
        <Global.System.ComponentModel.EditorBrowsableAttribute(Global.System.ComponentModel.EditorBrowsableState.Advanced)>  _
        Friend Property Culture() As Global.System.Globalization.CultureInfo
            Get
                Return resourceCulture
            End Get
            Set
                resourceCulture = value
            End Set
        End Property
        
        Friend ReadOnly Property _Get() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("_Get", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property _stop() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("_stop", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property add() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("add", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        '''<summary>
        '''  Looks up a localized string similar to &lt;TenPrintCard color=&quot;blue&quot;&gt;
        '''
        '''	&lt;block x=&quot;0&quot; y=&quot;0&quot; width=&quot;620&quot; height=&quot;61&quot;&gt;
        '''		&lt;cell width=&quot;127&quot;&gt;
        '''			&lt;text value=&quot;APPLICANT&quot; size=&quot;10&quot; alignment=&quot;center&quot; bold=&quot;true&quot;/&gt;
        '''			&lt;text value=&quot;FD-258 (REV.12-10-07)&quot; size=&quot;5&quot; alignment=&quot;bottom,left&quot;/&gt;
        '''		&lt;/cell&gt;
        '''		&lt;cell width=&quot;172&quot;&gt;
        '''			&lt;text value=&quot;LEAVE BLANK&quot; size=&quot;8&quot; alignment=&quot;top,center&quot;/&gt;
        '''		&lt;/cell&gt;
        '''		&lt;cell width=&quot;321&quot;&gt;
        '''			&lt;text value=&quot;TYPE OR PRINT ALL INFORMATION IN BLACK&quot; size=&quot;8&quot; alignment=&quot;center,top&quot;/&gt;
        '''			&lt;text value=&quot;LAST NAME                      [rest of string was truncated]&quot;;.
        '''</summary>
        Friend ReadOnly Property ApplicantCard() As String
            Get
                Return ResourceManager.GetString("ApplicantCard", resourceCulture)
            End Get
        End Property
        
        Friend ReadOnly Property close() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("close", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property closeMouseOn() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("closeMouseOn", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        '''<summary>
        '''  Looks up a localized string similar to &lt;TenPrintCard color=&quot;red&quot;&gt;
        '''	&lt;block x=&quot;0&quot; y=&quot;0&quot; width=&quot;322&quot; height=&quot;82&quot;&gt;
        '''		&lt;cell&gt;
        '''			&lt;text value=&quot;LEAVE BLANK          CRIMINAL&quot; size=&quot;8&quot; alignment=&quot;top,CENTER&quot; bold=&quot;true&quot;/&gt;
        '''		&lt;/cell&gt;
        '''	&lt;/block&gt;
        '''
        '''	&lt;block x=&quot;0&quot; y=&quot;65&quot; width=&quot;322&quot; height=&quot;17&quot;&gt;
        '''		&lt;cell width =&quot;68&quot;/&gt;
        '''		&lt;cell width=&quot;254&quot; drawRect=&quot;false&quot;&gt;
        '''			&lt;text value=&quot;FD-249 (Rev. 3-1-10)&quot; size=&quot;6&quot; alignment=&quot;bottom,left&quot; bold=&quot;true&quot;/&gt;
        '''		&lt;/cell&gt;
        '''	&lt;/block&gt;
        '''
        '''	&lt;block x=&quot;322&quot; y=&quot;0&quot; width=&quot;307&quot; height=&quot;82&quot;&gt;
        '''		&lt;cell height=&quot;32&quot; drawRect=&quot;false&quot;&gt;
        '''			&lt; [rest of string was truncated]&quot;;.
        '''</summary>
        Friend ReadOnly Property CriminalCard() As String
            Get
                Return ResourceManager.GetString("CriminalCard", resourceCulture)
            End Get
        End Property
        
        Friend ReadOnly Property Delete() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("Delete", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property GoToNext() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("GoToNext", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property GoToPrevious() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("GoToPrevious", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property Hands() As Byte()
            Get
                Dim obj As Object = ResourceManager.GetObject("Hands", resourceCulture)
                Return CType(obj,Byte())
            End Get
        End Property
        
        Friend ReadOnly Property Help() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("Help", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property NewDocumentHS() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("NewDocumentHS", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property newSource() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("newSource", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property openfolderHS() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("openfolderHS", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property play() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("play", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property remove() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("remove", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property Repeat() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("Repeat", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property saveHS() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("saveHS", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        Friend ReadOnly Property scanner() As System.Drawing.Icon
            Get
                Dim obj As Object = ResourceManager.GetObject("scanner", resourceCulture)
                Return CType(obj,System.Drawing.Icon)
            End Get
        End Property
        
        Friend ReadOnly Property settings() As System.Drawing.Bitmap
            Get
                Dim obj As Object = ResourceManager.GetObject("settings", resourceCulture)
                Return CType(obj,System.Drawing.Bitmap)
            End Get
        End Property
        
        '''<summary>
        '''  Looks up a localized string similar to &lt;TenPrintCard color=&quot;red&quot;&gt;
        '''
        '''	&lt;block x=&quot;0&quot; y =&quot;0&quot; width=&quot;800&quot; height=&quot;508&quot; isFingerBlock=&quot;true&quot;&gt;
        '''		&lt;cell width=&quot;161&quot; height=&quot;154&quot; fingerNo=&quot;1&quot;/&gt;
        '''		&lt;cell width=&quot;161&quot; height=&quot;154&quot; fingerNo=&quot;2&quot;/&gt;
        '''		&lt;cell width=&quot;160&quot; height=&quot;154&quot; fingerNo=&quot;3&quot;/&gt;
        '''		&lt;cell width=&quot;161&quot; height=&quot;154&quot; fingerNo=&quot;4&quot;/&gt;
        '''		&lt;cell width=&quot;157&quot; height=&quot;154&quot; fingerNo=&quot;5&quot;/&gt;
        '''		&lt;cell width=&quot;161&quot; height=&quot;150&quot; fingerNo=&quot;6&quot;/&gt;
        '''		&lt;cell width=&quot;161&quot; height=&quot;150&quot; fingerNo=&quot;7&quot;/&gt;
        '''		&lt;cell width=&quot;160&quot; height=&quot;150&quot; fingerNo=&quot;8&quot;/&gt;
        '''		&lt;cell width=&quot;161&quot; h [rest of string was truncated]&quot;;.
        '''</summary>
        Friend ReadOnly Property TenPrintCard() As String
            Get
                Return ResourceManager.GetString("TenPrintCard", resourceCulture)
            End Get
        End Property
    End Module
End Namespace